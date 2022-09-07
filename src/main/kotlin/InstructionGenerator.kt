/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
import java.util.*

fun generateInstructionsForScript(script: Script, sqlDialect: SqlDialect): Queue<Instruction> {
    val instruction = LinkedList<Instruction>()
    script.tables.forEach { table ->
        var primaryKeyFieldName = ""
        instruction.add(
            Instruction.Obj(
                objectName = table.tableName.fixCasing(),
                realName = table.tableName
            )
        )
        table.statements.forEach { statement ->
            instruction.add(
                Instruction.Stmt(
                    name = statement.identifier.fixCasing(),
                    typeDef = statement.type.convertToInstructionType(),
                    isNullable = statement.isNullable(),
                )
            )
            if (statement.isPrimaryKey()) {
                primaryKeyFieldName = statement.identifier.fixCasing()
            }
        }
        instruction.add(Instruction.Pk(fieldName = primaryKeyFieldName))
    }
    instruction.add(Instruction.Finalize)
    return instruction
}

private fun Type.convertToInstructionType() = when (this) {
    Type.Number -> TODO()
    is Type.Varchar -> "v$length"
}

private fun Statement.isPrimaryKey() = constraints.any { it is Constraint.PrimaryKey }
private fun Statement.isNullable() = constraints.any { it !is Constraint.NotNull }

private fun String.isLowerCase() = toCharArray().any { !it.isUpperCase() }
private fun String.capitalize() = toCharArray().let { chars ->
    chars[0].uppercase() + String(chars.slice((1 until chars.size)).toCharArray())
}

private fun String.fixCasing(): String =
    if (isLowerCase()) capitalize() else lowercase().capitalize()

