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

