import java.io.File

class Generator(
    private val filename: String,
    private val dialect: SqlDialect,
) {
    fun generate() {
        val sqlContents = File(filename).readText(Charsets.UTF_8)
        val tokens = Lexer.generateTokens(PreProcessor.preProcessSql(sqlContents))
        val script = Parser(tokens).parse()
        val instructions = generateInstructionsForScript(script, dialect)
        Interpreter(instructions).run()
    }
}
