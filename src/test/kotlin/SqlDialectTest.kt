import org.junit.jupiter.api.Test

class SqlDialectTest {

    @Test
    fun `try resolving a dialect that is not supported`() {
        val dialect = "OhNoSql".toDialect()

    }

}
