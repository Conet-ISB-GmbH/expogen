import lexer.OracleLexer
import lexer.PostgresLexer
import parser.OracleParser
import java.io.File

/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 * Copyright (c) 2022, Christoph Helbing <manig.christoph@googlemail.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
class Generator(
    private val filename: String,
    private val dialect: SqlDialect,
) {
    fun generate() {
        val sqlContents = File(filename).readText(Charsets.UTF_8)
        val tokens = getLexer(sqlDialect = dialect).generateTokens(PreProcessor.preProcessSql(sqlContents))
        val script = getParser(sqlDialect = dialect).parse(tokens)
        val instructions = generateInstructionsForScript(script)
        Interpreter(instructions).run()
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
