/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: ./expogen <SQLFILE>")
        exitProcess(-1)
    }

    args.first().let { filename ->
        val sqlContents = File(filename).readText(Charsets.UTF_8)
        val tokens = Lexer.generateTokens(sqlContents)
        val script = Parser(tokens).parse()
        val instructions = generateInstructionsForScript(script)
        Interpreter(instructions).run()
    }
}
