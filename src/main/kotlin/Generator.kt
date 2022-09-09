import lexer.Lexer
import lexer.OracleLexer
import lexer.PostgresLexer
import parser.OracleParser
import java.io.File

class Generator(
    private val filename: String,
    private val dialect: SqlDialect,
) {
    fun generate() {
        val sqlContents = File(filename).readText(Charsets.UTF_8)
        val tokens = getLexer(sqlDialect = dialect).generateTokens(PreProcessor.preProcessSql(sqlContents))
        val script = getParser(sqlDialect = dialect).parse(tokens)
//        val instructions = generateInstructionsForScript(script, dialect)
//        Interpreter(instructions).run()
    }
}
private fun getLexer(sqlDialect: SqlDialect) =
    when(sqlDialect) {
        SqlDialect.Oracle -> OracleLexer()
        SqlDialect.Postgresql -> PostgresLexer()
    }

private fun getParser(sqlDialect: SqlDialect) =
    when(sqlDialect) {
        SqlDialect.Oracle -> OracleParser()
        SqlDialect.Postgresql -> TODO()
    }
