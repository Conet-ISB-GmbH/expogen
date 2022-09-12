/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

import kotlin.system.exitProcess

sealed class Type {
    object Number : Type()
    data class NumberWithPrecision(val precision: Int, val scale: Int) : Type()
    data class Varchar(val length: Int) : Type()
    data class Varchar2(val length: Int) : Type()
    object Date : Type()
}

sealed class Constraint {
    object NotNull : Constraint()
    object PrimaryKey : Constraint()
    data class Default(val value: Int) : Constraint()
}

sealed class TableConstraint {
    data class Unique(val columnNames: List<String>) : TableConstraint()
}

data class ColumnStatement(
    val identifier: String,
    val type: Type,
    val constraints: List<Constraint>,
)

data class Table(
    val tableName: String,
    val columnStatements: List<ColumnStatement> = emptyList(),
    val tableContraints: List<TableConstraint> = emptyList(),
)

class Script {
    val tables: MutableList<Table> = mutableListOf()
}

abstract class Parser {
    private val script = Script()

    fun parse(tokens: MutableList<Token>): Script {
        while (tokens.isNotEmpty()) {
            parseTableStatement(tokens)
        }
        return script
    }

    private fun parseTableStatement(tokens: MutableList<Token>) {
        tokens.consume(2)
        val name = tokens.removeFirst()
        if (name is Token.Identifier) {
            val table = Table(tableName = name.value)
            val statements = mutableListOf<ColumnStatement>()
            val tableConstraints = mutableListOf<TableConstraint>()

            // Removes open bracket from create table statement
            tokens.consume()
            while (tokens.isNotEmpty() && tokens.first() !is Token.CloseBracket) {
                if(tokens.first() is Token.Identifier)
                    statements.add(parseColumnStatement(tokens))
                else
                    tableConstraints.add(parseTableConstraint(tokens))

            }
            script.tables.add(
                table.copy(
                    columnStatements = statements,
                    tableContraints = tableConstraints
                )
            )

            //Removes final closing bracket of create table statement
            tokens.consume()
        } else {
            println("wow expected and identifier but got $name")
            exitProcess(-1)
        }
    }

    abstract fun parseColumnStatement(tokens: MutableList<Token>): ColumnStatement

    abstract fun parseTableConstraint(tokens: MutableList<Token>): TableConstraint

    protected fun createDefaultConstraint(tokens: MutableList<Token>) : Constraint {
        val defaultValue = (tokens.removeFirst() as Token.Identifier).value.toInt()
        return Constraint.Default(defaultValue)
    }

    protected fun MutableList<Token>.consume(number: Int = 1) {
        repeat((0 until number).count()) { removeFirst() }
    }


}
