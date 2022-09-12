package parser

import Constraint
import Parser
import ColumnStatement
import TableConstraint
import Token
import Type

class OracleParser : Parser() {
    override fun parseColumnStatement(tokens: MutableList<Token>): ColumnStatement {
        val colIdentifier = (tokens.removeFirst() as Token.Identifier)
        val colType = (tokens.removeFirst() as Token.Identifier)

        val type = when (colType.value) {
            "varchar" -> createVarchar(tokens)
            "varchar2" -> createVarchar2(tokens)
            "number" -> createNumber(tokens)
            "date" -> createDate()
            else -> throw RuntimeException("ColumnType: ${colType.value} is not supported by oracle dialect")
        }

        val constraints = parseColumnConstraints(tokens)

        return ColumnStatement(
            identifier = colIdentifier.value,
            type = type,
            constraints = constraints,
        )
    }

    override fun parseTableConstraint(tokens: MutableList<Token>): TableConstraint {
        return when(val constraintIdentifier = tokens.removeFirst()) {
            is Token.Unique -> createUniqueTableConstraint(tokens)
            else -> throw RuntimeException("Table-Constraint: $constraintIdentifier is not supported by oracle dialect")
        }
    }

    private fun createUniqueTableConstraint(tokens: MutableList<Token>): TableConstraint {
        val columnNames = mutableListOf<String>()
        // Removes OpenBracket
        tokens.consume()
        while(tokens.first() is Token.Identifier) {
            columnNames.add((tokens.removeFirst() as Token.Identifier).value)
        }
        // Removes CloseBracket
        tokens.consume()
        return TableConstraint.Unique(columnNames = columnNames)
    }

    private fun parseColumnConstraints(tokens: MutableList<Token>): List<Constraint> {
        val constraints = mutableListOf<Constraint>()
        while (tokens.isNotEmpty() && isColumnConstraintNext(tokens)) {
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

    private fun createDate(): Type = Type.Date

    private fun isColumnConstraintNext(tokens: MutableList<Token>) =
        tokens.first() !is Token.Identifier && tokens.first() !is Token.CloseBracket && !isTableConstraintNext(tokens)

    private fun isTableConstraintNext(tokens: MutableList<Token>) =
        tokens.first() is Token.Unique
}