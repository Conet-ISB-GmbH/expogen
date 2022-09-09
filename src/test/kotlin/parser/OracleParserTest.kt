package parser

import Constraint
import TableConstraint
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
        assertEquals(0, instructions.tables[0].columnStatements.size)
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
        assertEquals(1, instructions.tables[0].columnStatements.size)
        assertEquals("id", instructions.tables[0].columnStatements[0].identifier)
        assertEquals(Type.Varchar(128), instructions.tables[0].columnStatements[0].type)
        assertEquals(2, instructions.tables[0].columnStatements[0].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[0].constraints[0])
        assertEquals(Constraint.PrimaryKey, instructions.tables[0].columnStatements[0].constraints[1])

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
        assertEquals(5, instructions.tables[0].columnStatements.size)

        assertEquals("id", instructions.tables[0].columnStatements[0].identifier)
        assertEquals(Type.Varchar(128), instructions.tables[0].columnStatements[0].type)
        assertEquals(2, instructions.tables[0].columnStatements[0].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[0].constraints[0])
        assertEquals(Constraint.PrimaryKey, instructions.tables[0].columnStatements[0].constraints[1])

        assertEquals("kind", instructions.tables[0].columnStatements[1].identifier)
        assertEquals(Type.Varchar2(32), instructions.tables[0].columnStatements[1].type)
        assertEquals(1, instructions.tables[0].columnStatements[1].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[1].constraints[0])

        assertEquals("year", instructions.tables[0].columnStatements[2].identifier)
        assertEquals(Type.Number, instructions.tables[0].columnStatements[2].type)
        assertEquals(0, instructions.tables[0].columnStatements[2].constraints.size)

        assertEquals("is_valid", instructions.tables[0].columnStatements[3].identifier)
        assertEquals(Type.NumberWithPrecision(1, 0), instructions.tables[0].columnStatements[3].type)
        assertEquals(1, instructions.tables[0].columnStatements[3].constraints.size)
        assertEquals(Constraint.Default(1), instructions.tables[0].columnStatements[3].constraints[0])

        assertEquals("admission_date", instructions.tables[0].columnStatements[4].identifier)
        assertEquals(Type.Date, instructions.tables[0].columnStatements[4].type)
        assertEquals(1, instructions.tables[0].columnStatements[4].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[4].constraints[0])
    }

    @Test
    fun `create table with unique constraint with multiple columns`() {
        val sqlString = """
            CREATE TABLE TEST
            (
                ID              VARCHAR(128)        NOT NULL PRIMARY KEY,
                UNIQUE (OFFICE_NUMBER, KIND, YEAR)                
            );
        """.trimIndent()
        val tokens = OracleLexer().generateTokens(sqlString)
        val instructions = OracleParser().parse(tokens)

        assertNotNull(instructions)
        assertEquals(1, instructions.tables.size)
        assertEquals("test", instructions.tables[0].tableName)
        assertEquals(1, instructions.tables[0].columnStatements.size)

        assertEquals("id", instructions.tables[0].columnStatements[0].identifier)
        assertEquals(Type.Varchar(128), instructions.tables[0].columnStatements[0].type)
        assertEquals(2, instructions.tables[0].columnStatements[0].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[0].constraints[0])
        assertEquals(Constraint.PrimaryKey, instructions.tables[0].columnStatements[0].constraints[1])

        assertEquals(1, instructions.tables[0].tableContraints.size)
        assertEquals(3, (instructions.tables[0].tableContraints[0] as TableConstraint.Unique).columnNames.size)
        assertEquals("office_number", (instructions.tables[0].tableContraints[0] as TableConstraint.Unique).columnNames[0])
        assertEquals("kind", (instructions.tables[0].tableContraints[0] as TableConstraint.Unique).columnNames[1])
        assertEquals("year", (instructions.tables[0].tableContraints[0] as TableConstraint.Unique).columnNames[2])
    }

    @Test
    fun `create table with unique constraint with one column`() {
        val sqlString = """
            CREATE TABLE TEST
            (
                ID              VARCHAR(128)        NOT NULL PRIMARY KEY,
                UNIQUE (KIND)                
            );
        """.trimIndent()
        val tokens = OracleLexer().generateTokens(sqlString)
        val instructions = OracleParser().parse(tokens)

        assertNotNull(instructions)
        assertEquals(1, instructions.tables.size)
        assertEquals("test", instructions.tables[0].tableName)
        assertEquals(1, instructions.tables[0].columnStatements.size)

        assertEquals("id", instructions.tables[0].columnStatements[0].identifier)
        assertEquals(Type.Varchar(128), instructions.tables[0].columnStatements[0].type)
        assertEquals(2, instructions.tables[0].columnStatements[0].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[0].constraints[0])
        assertEquals(Constraint.PrimaryKey, instructions.tables[0].columnStatements[0].constraints[1])

        assertEquals(1, instructions.tables[0].tableContraints.size)
        assertEquals(1, (instructions.tables[0].tableContraints[0] as TableConstraint.Unique).columnNames.size)
        assertEquals("kind", (instructions.tables[0].tableContraints[0] as TableConstraint.Unique).columnNames[0])
    }

    @Test
    fun `create table with foreign key constraint`() {
        val sqlString = """
            CREATE TABLE TEST
            (
                ID              VARCHAR(128)      NOT NULL PRIMARY KEY,
                THIS_ID         NUMBER            NOT NULL,
                CONSTRAINT fk_other_id FOREIGN KEY (THIS_ID) REFERENCES OTHER_TABLE (OTHER_ID),                
            );
        """.trimIndent()
        val tokens = OracleLexer().generateTokens(sqlString)
        val instructions = OracleParser().parse(tokens)

        assertNotNull(instructions)
        assertEquals(1, instructions.tables.size)
        assertEquals("test", instructions.tables[0].tableName)
        assertEquals(2, instructions.tables[0].columnStatements.size)

        assertEquals("id", instructions.tables[0].columnStatements[0].identifier)
        assertEquals(Type.Varchar(128), instructions.tables[0].columnStatements[0].type)
        assertEquals(2, instructions.tables[0].columnStatements[0].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[0].constraints[0])
        assertEquals(Constraint.PrimaryKey, instructions.tables[0].columnStatements[0].constraints[1])

        assertEquals("this_id", instructions.tables[0].columnStatements[1].identifier)
        assertEquals(Type.Number, instructions.tables[0].columnStatements[1].type)
        assertEquals(1, instructions.tables[0].columnStatements[1].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[1].constraints[0])

        assertEquals(1, instructions.tables[0].tableContraints.size)
        assertEquals(TableConstraint.ForeignKey(
            name = "fk_other_id",
            columnName = "this_id",
            foreignTableName = "other_table",
            foreignKeyColumn = "other_id"
        ), instructions.tables[0].tableContraints[0])
    }

    @Test
    fun `create table with unique and foreign key constraint`() {
        val sqlString = """
            CREATE TABLE TEST
            (
                ID              VARCHAR(128)      NOT NULL PRIMARY KEY,
                THIS_ID         NUMBER            NOT NULL,
                UNIQUE (THIS_ID)                
                CONSTRAINT fk_other_id FOREIGN KEY (THIS_ID) REFERENCES OTHER_TABLE (OTHER_ID),                
            );
        """.trimIndent()
        val tokens = OracleLexer().generateTokens(sqlString)
        val instructions = OracleParser().parse(tokens)

        assertNotNull(instructions)
        assertEquals(1, instructions.tables.size)
        assertEquals("test", instructions.tables[0].tableName)
        assertEquals(2, instructions.tables[0].columnStatements.size)

        assertEquals("id", instructions.tables[0].columnStatements[0].identifier)
        assertEquals(Type.Varchar(128), instructions.tables[0].columnStatements[0].type)
        assertEquals(2, instructions.tables[0].columnStatements[0].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[0].constraints[0])
        assertEquals(Constraint.PrimaryKey, instructions.tables[0].columnStatements[0].constraints[1])

        assertEquals("this_id", instructions.tables[0].columnStatements[1].identifier)
        assertEquals(Type.Number, instructions.tables[0].columnStatements[1].type)
        assertEquals(1, instructions.tables[0].columnStatements[1].constraints.size)
        assertEquals(Constraint.NotNull, instructions.tables[0].columnStatements[1].constraints[0])

        assertEquals(2, instructions.tables[0].tableContraints.size)
        assertEquals(1, (instructions.tables[0].tableContraints[0] as TableConstraint.Unique).columnNames.size)
        assertEquals("this_id", (instructions.tables[0].tableContraints[0] as TableConstraint.Unique).columnNames[0])

        assertEquals(TableConstraint.ForeignKey(
            name = "fk_other_id",
            columnName = "this_id",
            foreignTableName = "other_table",
            foreignKeyColumn = "other_id"
        ), instructions.tables[0].tableContraints[1])
    }
}