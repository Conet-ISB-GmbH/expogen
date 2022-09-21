package lexer

import Token

/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 * Copyright (c) 2022, Christoph Helbing <manig.christoph@googlemail.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
class OracleLexer : Lexer() {
    override fun generateDialectTokens(currentToken: String, sqlContents: String): Boolean {
        return when (currentToken) {
            "unique" -> {
                tokens.add(Token.Unique)
                reset()
                true
            }

            "constraint" -> {
                tokens.add(Token.Constraint)
                reset()
                true
            }

            else -> false
        }
    }
}