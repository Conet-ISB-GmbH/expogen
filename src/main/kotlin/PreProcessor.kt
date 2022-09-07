/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
object PreProcessor {
    fun preProcessSql(sqlString: String): String =
        sqlString
            .lines()
            .filter { line -> line.trim().isNotLineComment() }.joinToString("\n") {
                if (it.contains("--")) {
                    it.split("--")[0].trimEnd()
                } else {
                    it
                }
            }

    private fun String.isNotLineComment() = !startsWith("--")
}

