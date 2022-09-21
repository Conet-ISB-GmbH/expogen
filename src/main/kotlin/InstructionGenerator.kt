import parser.*
import java.util.LinkedList
import java.util.Queue

/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 * Copyright (c) 2022, Christoph Helbing <manig.christoph@googlemail.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

fun generateInstructionsForScript(script: Script): Queue<Instruction> {
    val instruction = LinkedList<Instruction>()
    script.tables.forEach { table ->
        var primaryKeyFieldName = ""
        instruction.add(
            Instruction.Obj(
                objectName = table.tableName,
                realName = table.tableName
            )
        )
        table.columnStatements.forEach { statement ->
            instruction.add(
                Instruction.ColStmt(
                    name = statement.identifier,
                    typeDef = statement.type.convertToInstructionType(),
                    isNullable = statement.isNullable(),
                    defaultValue = statement.getDefaultValue()
                )
            )
            if (statement.isPrimaryKey()) {
                primaryKeyFieldName = statement.identifier
            }
        }
        instruction.add(Instruction.Pk(fieldName = primaryKeyFieldName))

        table.tableContraints.forEach { tableConstraint ->
            instruction.add(
                when (tableConstraint) {
                    is TableConstraint.Unique -> Instruction.Unique(columnNames = tableConstraint.columnNames)
                    is TableConstraint.ForeignKey -> Instruction.Fk(
                        name = tableConstraint.name,
                        columnName = tableConstraint.columnName,
                        foreignTableName = tableConstraint.foreignTableName,
                        foreignKeyColumn = tableConstraint.foreignKeyColumn,
                    )
                }
            )
        }
    }
    instruction.add(Instruction.Finalize)
    return instruction
}

private fun Type.convertToInstructionType() =
    when (this) {
        is Type.Number -> "n"
        is Type.Varchar -> "v$length"
        is Type.Varchar2 -> "v$length"
        is Type.Date -> "d"
        is Type.NumberWithPrecision -> "p$precision;$scale"
    }

private fun ColumnStatement.isPrimaryKey() = constraints.any { it is Constraint.PrimaryKey }
private fun ColumnStatement.isNullable() = !constraints.contains(Constraint.NotNull)
private fun ColumnStatement.getDefaultValue() = constraints.filterIsInstance<Constraint.Default>().firstOrNull()?.value