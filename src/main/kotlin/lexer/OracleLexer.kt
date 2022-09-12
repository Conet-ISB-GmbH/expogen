package lexer

import Token

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