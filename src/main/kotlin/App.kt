import kotlin.system.exitProcess

/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 * Copyright (c) 2022, Christoph Helbing <manig.christoph@googlemail.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Usage: ./expogen <SQLFILE> <DIALECT[ORACLE,POSTGRESQL]>")
        exitProcess(-1)
    }

    val filename = args.first()
    val dialect = args.second().toDialect()

    Generator(filename = filename, dialect = dialect.getOrThrow())
        .generate()
}
