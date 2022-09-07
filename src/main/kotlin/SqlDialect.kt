import java.lang.IllegalArgumentException

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

