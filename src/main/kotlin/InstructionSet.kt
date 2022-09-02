/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
sealed class Instruction {
    data class Obj(val objectName: String, val realName: String) : Instruction()
    data class Stmt(val name: String, val typeDef: String, val isNullable: Boolean = false) :
        Instruction()

    data class Pk(val fieldName: String) : Instruction()
    object Finalize : Instruction()
}
