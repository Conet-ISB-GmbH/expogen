/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
sealed class Token {
    data class Identifier(val value: String) : Token()
    object CreateKeyword : Token()
    object TableKeyword : Token()
    object PrimaryKey : Token()
    object NotNull : Token()
    object OpenBracket : Token()
    object CloseBracket : Token()

    override fun toString(): String {
        return this::class.simpleName!!
    }
}
