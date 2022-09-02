/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
import java.util.*

class Interpreter(
    private val instructionSet: Queue<Instruction>,
) {
    fun run() {
        val code = blindlyPlaceImportsAndGetBuilder()
        while (instructionSet.isNotEmpty()) {
            when (val instruction = instructionSet.poll()) {
                is Instruction.Obj -> {
                    code.append("object ")
                        .append(instruction.objectName)
                        .append(" : Table(name = \"${instruction.realName}\") {\n")
                }

                is Instruction.Pk -> {
                    code.append("\toverride val primaryKey = PrimaryKey(${instruction.fieldName})")
                }

                is Instruction.Stmt -> {
                    code.append("\tval ${instruction.name} = ${toExposedType(instruction)}\n")
                }

                Instruction.Finalize -> code.append("\n}")
            }
        }
        println(code.toString())
    }

    private fun toExposedType(stmt: Instruction.Stmt): String {
        val typeDef = stmt.typeDef
        val typePrefix = when (val type = typeDef.first()) {
            'v' -> {
                val length = if (typeDef.length > 1) {
                    typeDef.removePrefix(type.toString())
                } else 0
                "varchar(name = \"${stmt.name}\", length = $length)"
            }

            else -> ""
        }
        return if (stmt.isNullable) {
            "$typePrefix.nullable()"
        } else typePrefix
    }

    private fun blindlyPlaceImportsAndGetBuilder(): StringBuilder {
        val builder = StringBuilder()
        builder.append("import org.jetbrains.exposed.sql.Table\n")
            .append("import org.jetbrains.exposed.sql.javatime.date\n\n")
        return builder
    }
}

