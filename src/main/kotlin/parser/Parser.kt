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
}

data class Statement(
    val identifier: String,
    val type: Type,
    val constraints: List<Constraint>,
)

data class Table(
    val tableName: String,
    val statements: List<Statement> = emptyList(),
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
            val statements = mutableListOf<Statement>()

            // Removes open bracket from create table statement
            tokens.consume()
            while (tokens.isNotEmpty() && tokens.first() !is Token.CloseBracket)
                statements.add(parseInnerStatement(tokens))
            script.tables.add(table.copy(statements = statements))

            //Removes final closing bracket of create table statement
            tokens.consume()
        } else {
            println("wow expected and identifier but got $name")
            exitProcess(-1)
        }
    }

    abstract fun parseInnerStatement(tokens: MutableList<Token>): Statement

//    private fun parseInnerStatement(): Statement {
//        val colIdentifier = (tokens.removeFirst() as Token.Identifier)
//        val colType = (tokens.removeFirst() as Token.Identifier)
//        val constraints = mutableListOf<Constraint>()
//        while (tokens.isNotEmpty() && tokens.first() !is Token.Identifier) {
//            when (tokens.removeFirst()) {
//                Token.NotNull -> constraints.add(Constraint.NotNull)
//                Token.PrimaryKey -> constraints.add(Constraint.PrimaryKey)
//                else -> {}
//            }
//        }
//        val type = if (colType.value.contains("varchar")) {
//            val parts = colType.value.removeSuffix(")").split("(")
//            val length = parts[1].toInt()
//            Type.Varchar(length = length)
//        } else {
//            Type.Number
//        }
//        return Statement(
//            identifier = colIdentifier.value,
//            type = type,
//            constraints = constraints,
//        )
//    }

    protected fun MutableList<Token>.consume(number: Int = 1) {
        repeat((0 until number).count()) { removeFirst() }
    }


}
