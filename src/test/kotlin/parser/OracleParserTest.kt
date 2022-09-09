package parser

import lexer.OracleLexer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OracleParserTest {
    @Test
    fun `generate create table instruction`() {
        val sqlString = "CREATE TABLE Test (\n);"
        val tokens = OracleLexer().generateTokens(sqlString)
        val instructions = OracleParser().parse(tokens)

        assertNotNull(instructions)
        assertEquals(1, instructions.tables.size)
        assertEquals("test", instructions.tables[0].tableName)
        assertEquals(0, instructions.tables[0].statements.size)
    }
    @Test
    fun `generate create table instruction with primary key statement`() {
        val sqlString = """
            CREATE TABLE TEST
            (
                ID  VARCHAR(128) NOT NULL PRIMARY KEY                
            );
        """.trimIndent()
        val tokens = OracleLexer().generateTokens(sqlString)
        val instructions = OracleParser().parse(tokens)

        assertNotNull(instructions)
        assertEquals(1, instructions.tables.size)
        assertEquals("test", instructions.tables[0].tableName)
        assertEquals(1, instructions.tables[0].statements.size)
    }
}