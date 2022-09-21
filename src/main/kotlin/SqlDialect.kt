import java.lang.IllegalArgumentException

/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 * Copyright (c) 2022, Christoph Helbing <manig.christoph@googlemail.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
enum class SqlDialect {
    Oracle, Postgresql;

    companion object {
        fun containsKey(key: String) = values().any { it.name == key }
    }
}

fun String.toDialect(): Result<SqlDialect> {
    val value = lowercase()
    return "${value.first().titlecaseChar()}${value.slice(1 until value.length)}"
        .let { key ->
            if (SqlDialect.containsKey(key))
                Result.success(SqlDialect.valueOf(key))
            else Result.failure(IllegalArgumentException("Unknown SQL dialect $this"))
        }
}

