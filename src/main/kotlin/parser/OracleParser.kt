package parser

import Constraint
import Parser
import Statement
import Token
import Type
import java.lang.RuntimeException

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



        return Statement(
            identifier = colIdentifier.value,
            type = type,
            constraints = constraints,
        )
    }

    private fun createNumber(tokens: MutableList<Token>): Type =
        if(tokens.first() is Token.OpenBracket) {
            tokens.consume()
            val precision = (tokens.removeFirst() as Token.Identifier).value.toInt()
            val scale = (tokens.removeFirst() as Token.Identifier).value.toInt()
            Type.NumberWithPrecision(precision, scale)
        } else {
            Type.Number
        }


    private fun createVarchar(tokens: MutableList<Token>): Type {
        TODO()
    }

    private fun createVarchar2(tokens: MutableList<Token>): Type {
        TODO()
    }

    private fun createDate(tokens: MutableList<Token>): Type {
        TODO()
    }
}