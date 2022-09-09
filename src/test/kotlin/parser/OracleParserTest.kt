package parser

import Constraint
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
        assertEquals("id", instructions.tables[0].statements[0].identifier)
        assertEquals(Type.Varchar(128), instructions.tables[0].statements[0].type)
        assertEquals(2, instructions.tables[0].statements[0].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].statements[0].constraints[0])
        assertEquals(Constraint.PrimaryKey, instructions.tables[0].statements[0].constraints[1])

    }

    @Test
    fun `generate create table instruction with multiple statements`() {
        val sqlString = """
            CREATE TABLE TEST
            (
                ID              VARCHAR(128)        NOT NULL PRIMARY KEY,
                KIND            VARCHAR2(32 CHAR)   NOT NULL,
                YEAR            NUMBER,
                IS_VALID        NUMBER(1, 0)        DEFAULT 1,
                ADMISSION_DATE  DATE                NOT NULL,
            );
        """.trimIndent()
        val tokens = OracleLexer().generateTokens(sqlString)
        val instructions = OracleParser().parse(tokens)

        assertNotNull(instructions)
        assertEquals(1, instructions.tables.size)
        assertEquals("test", instructions.tables[0].tableName)
        assertEquals(5, instructions.tables[0].statements.size)

        assertEquals("id", instructions.tables[0].statements[0].identifier)
        assertEquals(Type.Varchar(128), instructions.tables[0].statements[0].type)
        assertEquals(2, instructions.tables[0].statements[0].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].statements[0].constraints[0])
        assertEquals(Constraint.PrimaryKey, instructions.tables[0].statements[0].constraints[1])

        assertEquals("kind", instructions.tables[0].statements[1].identifier)
        assertEquals(Type.Varchar2(32), instructions.tables[0].statements[1].type)
        assertEquals(1, instructions.tables[0].statements[1].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].statements[1].constraints[0])

        assertEquals("year", instructions.tables[0].statements[2].identifier)
        assertEquals(Type.Number, instructions.tables[0].statements[2].type)
        assertEquals(0, instructions.tables[0].statements[2].constraints.size)

        assertEquals("is_valid", instructions.tables[0].statements[3].identifier)
        assertEquals(Type.NumberWithPrecision(1, 0), instructions.tables[0].statements[3].type)
        assertEquals(1, instructions.tables[0].statements[3].constraints.size)
        assertEquals(Constraint.Default(1), instructions.tables[0].statements[3].constraints[0])

        assertEquals("admission_date", instructions.tables[0].statements[4].identifier)
        assertEquals(Type.Date, instructions.tables[0].statements[4].type)
        assertEquals(1, instructions.tables[0].statements[4].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].statements[4].constraints[0])
    }
}