import java.util.*

/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 * Copyright (c) 2022, Christoph Helbing <manig.christoph@googlemail.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
class Interpreter(
    private val instructionSet: Queue<Instruction>,
) {
    fun run() {
        val code = blindlyPlaceImportsAndGetBuilder()
        while (instructionSet.isNotEmpty()) {
            when (val instruction = instructionSet.poll()) {
                is Instruction.Obj -> {
                    code.append("object ")
                        .append(instruction.objectName.fixCasing().let { it.first().titlecaseChar() + it.slice(1 until it.length) })
                        .append(" : Table(name = \"${instruction.realName.uppercase()}\") {\n")
                }

                is Instruction.Pk -> {
                    code.append("\toverride val primaryKey = PrimaryKey(${instruction.fieldName.uppercase()})")
                }

                is Instruction.ColStmt -> {
                    code.append("\tval ${instruction.name.fixCasing()} = ${toExposedType(instruction)}\n")
                }

                Instruction.Finalize -> code.append("\n}")

                else -> {}
            }
        }
        println(code.toString())
    }

    private fun toExposedType(stmt: Instruction.ColStmt): String {
        val typeDef = stmt.typeDef
        val typePrefix = when (val type = typeDef.first()) {
            'v' -> {
                val length = if (typeDef.length > 1) {
                    typeDef.removePrefix(type.toString())
                } else 0
                "varchar(name = \"${stmt.name.uppercase()}\", length = $length)"
            }

            'n' -> "integer(name = \"${stmt.name.uppercase()}\")"
            'd' -> "date(name = \"${stmt.name.uppercase()}\")"
            'p' -> "integer(name = \"${stmt.name.uppercase()}\")"
            else -> ""
        }
        return if (stmt.isNullable) {
            "$typePrefix.nullable()"
        } else typePrefix
    }

    private fun blindlyPlaceImportsAndGetBuilder(): StringBuilder {
        val builder = StringBuilder()
        builder.append("import org.jetbrains.exposed.sql.parser.Table\n")
            .append("import org.jetbrains.exposed.sql.javatime.date\n\n")
        return builder
    }

    private tailrec fun String.fixCasing(): String {
        val index = indexOf("_")
        if (index < 0)
            return this

        return replaceRange(index + 1 until index + 2, get(index + 1).uppercase())
            .removeRange(index until index + 1).fixCasing()
    }
}

