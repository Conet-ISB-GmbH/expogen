/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 * Copyright (c) 2022, Christoph Helbing <manig.christoph@googlemail.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
sealed class Instruction {
    data class Obj(
        val objectName: String,
        val realName: String
    ) : Instruction()

    data class ColStmt(
        val name: String,
        val typeDef: String,
        val isNullable: Boolean = false,
        val defaultValue: Any?,
    ) : Instruction()

    data class Pk(val fieldName: String) : Instruction()

    data class Fk(
        val name: String,
        val columnName: String,
        val foreignTableName: String,
        val foreignKeyColumn: String,
    ) : Instruction()

    data class Unique(
        val columnNames: List<String>
    ) : Instruction()

    object Finalize : Instruction()
}
