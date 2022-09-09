package parser

import Constraint
import Parser
import Statement
import Token
import Type
import java.lang.RuntimeException
import java.util.TooManyListenersException

class OracleParser : Parser() {
    override fun parseInnerStatement(tokens: MutableList<Token>): Statement {
        val colIdentifier = (tokens.removeFirst() as Token.Identifier)
        val colType = (tokens.removeFirst() as Token.Identifier)

        val type = when (colType.value) {
            "varchar" -> createVarchar(tokens)
            "varchar2" -> createVarchar2(tokens)
            "number" -> createNumber(tokens)
            "date" -> createDate(tokens)
            else -> throw RuntimeException("ColumnType: ${colType.value} is not supported by oracle dialect")
        }

        val constraints = parseStatementContraints(tokens)

        return Statement(
            identifier = colIdentifier.value,
            type = type,
            constraints = constraints,
        )
    }

    private fun parseStatementContraints(tokens: MutableList<Token>): List<Constraint> {
        val constraints = mutableListOf<Constraint>()
        while (tokens.isNotEmpty() && tokens.first() !is Token.Identifier && tokens.first() !is Token.CloseBracket) {
            when (tokens.removeFirst()) {
                Token.NotNull -> constraints.add(Constraint.NotNull)
                Token.PrimaryKey -> constraints.add(Constraint.PrimaryKey)
                Token.Default -> constraints.add(createDefaultConstraint(tokens))
                else -> {}
            }
        }
        return constraints
    }

    private fun createNumber(tokens: MutableList<Token>): Type =
        if(tokens.first() is Token.OpenBracket) {
            tokens.consume()
            val precision = (tokens.removeFirst() as Token.Identifier).value.toInt()
            val scale = (tokens.removeFirst() as Token.Identifier).value.toInt()
            tokens.consume()
            Type.NumberWithPrecision(precision, scale)
        } else {
            Type.Number
        }

    private fun createVarchar(tokens: MutableList<Token>): Type {
        tokens.consume()
        val length = (tokens.removeFirst() as Token.Identifier).value.toInt()
        tokens.consume()
        return Type.Varchar(length)
    }

    private fun createVarchar2(tokens: MutableList<Token>): Type {
        tokens.consume()
        val length = (tokens.removeFirst() as Token.Identifier).value.toInt()
        tokens.consume(2)
        return Type.Varchar2(length)
    }

    private fun createDate(tokens: MutableList<Token>): Type = Type.Date
}