package lexer

import Token

class OracleLexer : Lexer() {
    override fun generateDialectTokens(currentToken: String, sqlContents: String): Boolean {
        return when (currentToken) {
            "unique" -> {
                consumeToken(sqlContents)
                tokens.add(Token.Unique)
                reset()
                true
            }

            else -> false
        }
    }
}