package lexer

import Token

/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
abstract class Lexer {
    protected val tokens = mutableListOf<Token>()
    private var currentToken = ""
    private var parsePos = 0

    abstract fun generateDialectTokens(currentToken: String, sqlContents: String): Boolean

    protected fun reset() {
        currentToken = ""
    }

    fun generateTokens(sqlContents: String): MutableList<Token> {
        while (parsePos < sqlContents.length) {
            currentToken += sqlContents[parsePos].lowercase()
            when (currentToken) {
                "create" -> {
                    tokens.add(Token.CreateKeyword)
                    reset()
                }

                "table" -> {
                    tokens.add(Token.TableKeyword)
                    reset()
                }

                "not" -> {
                    parsePos = consumeToken(sqlContents)
                    tokens.add(Token.NotNull)
                    reset()
                }

                "primary" -> {
                    parsePos = consumeToken(sqlContents)
                    tokens.add(Token.PrimaryKey)
                    reset()
                }

                "(" -> {
                    tokens.add(Token.OpenBracket)
                    reset()
                }

                ")" -> {
                    tokens.add(Token.CloseBracket)
                    reset()
                }

                " ", "\n", "," -> {
                    reset()
                }

                else -> {
                    if (generateDialectTokens(currentToken, sqlContents))
                        continue

                    if (
                        parsePos + 1 < sqlContents.length &&
                        (sqlContents[parsePos + 1] == ' ' ||
                                sqlContents[parsePos + 1] == '\n' ||
                                sqlContents[parsePos + 1] == ',' ||
                                sqlContents[parsePos + 1] == '(' ||
                                sqlContents[parsePos + 1] == ')')
                    ) {
                            tokens.add(Token.Identifier(currentToken))
                            reset()
                    }
                }
            }
            parsePos++
        }

        return tokens
    }

    protected fun consumeToken(
        sqlContents: String,
    ): Int {
        var currentToken1 = currentToken
        if (parsePos + 1 < sqlContents.length && sqlContents[parsePos + 1] == ' ') {
            parsePos++
        }
        do {
            currentToken1 += sqlContents[parsePos].lowercase()
            parsePos++
        } while (
            sqlContents[parsePos] != ' ' &&
            sqlContents[parsePos] != ',' &&
            sqlContents[parsePos] != '\n' &&
            sqlContents[parsePos] != '(' &&
            sqlContents[parsePos] != ')'
        )

        return parsePos
    }
}
