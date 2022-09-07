import kotlin.test.Test
import kotlin.test.assertEquals

class PreProcessorTest {
    @Test
    fun `pass plain sql into the pre processor and nothing should change`() {
        val sqlForTesting = """
            CREATE TABLE TEST_TABLE
            (
                ID                     VARCHAR(128)      NOT NULL PRIMARY KEY,
                OFFICE_NUMBER          VARCHAR2(2 CHAR)  NOT NULL,
                KIND                   VARCHAR2(32 CHAR) NOT NULL,
                YEAR                   NUMBER            NOT NULL,
                MAX_NUMBER             number            NOT NULL,
                IS_VALID               NUMBER(1, 0) DEFAULT 1,
                ADMISSION_DATE         DATE              NOT NULL,
                ADMISSION_EDITOR_ID    VARCHAR(128)      NOT NULL,
                MODIFICATION_DATE      DATE,
                MODIFICATION_EDITOR_ID VARCHAR(128),
                UNIQUE (OFFICE_NUMBER, KIND, YEAR)
            );
        """.trimIndent()

        val processedSql = PreProcessor.preProcessSql(sqlForTesting)

        assertEquals(sqlForTesting, processedSql)
    }

    @Test
    fun `pass an sql statement with line comments`() {
        val sqlForTesting = """
            -- this is a long line comment
            CREATE TABLE TEST_TABLE
            (
                -- this is yet another line comment
                ID                     VARCHAR(128)      NOT NULL PRIMARY KEY,
                OFFICE_NUMBER          VARCHAR2(2 CHAR)  NOT NULL,
                KIND                   VARCHAR2(32 CHAR) NOT NULL,
                YEAR                   NUMBER            NOT NULL,
                MAX_NUMBER             number            NOT NULL,
                IS_VALID               NUMBER(1, 0) DEFAULT 1,
                ADMISSION_DATE         DATE              NOT NULL,
                ADMISSION_EDITOR_ID    VARCHAR(128)      NOT NULL,
                MODIFICATION_DATE      DATE,
                MODIFICATION_EDITOR_ID VARCHAR(128),
                UNIQUE (OFFICE_NUMBER, KIND, YEAR)
            );
        """.trimIndent()

        val expectedSql = """
            CREATE TABLE TEST_TABLE
            (
                ID                     VARCHAR(128)      NOT NULL PRIMARY KEY,
                OFFICE_NUMBER          VARCHAR2(2 CHAR)  NOT NULL,
                KIND                   VARCHAR2(32 CHAR) NOT NULL,
                YEAR                   NUMBER            NOT NULL,
                MAX_NUMBER             number            NOT NULL,
                IS_VALID               NUMBER(1, 0) DEFAULT 1,
                ADMISSION_DATE         DATE              NOT NULL,
                ADMISSION_EDITOR_ID    VARCHAR(128)      NOT NULL,
                MODIFICATION_DATE      DATE,
                MODIFICATION_EDITOR_ID VARCHAR(128),
                UNIQUE (OFFICE_NUMBER, KIND, YEAR)
            );
        """.trimIndent()

        val processedSql = PreProcessor.preProcessSql(sqlForTesting)

        assertEquals(expectedSql, processedSql)
    }

    @Test
    fun `passing an sql string with inline comments`() {
        val sqlForTesting = """
            CREATE TABLE TEST_TABLE -- first inline comment
            (
                ID                     VARCHAR(128)      NOT NULL PRIMARY KEY, -- yet another inline comment
                OFFICE_NUMBER          VARCHAR2(2 CHAR)  NOT NULL,
                KIND                   VARCHAR2(32 CHAR) NOT NULL,
                YEAR                   NUMBER            NOT NULL,
                MAX_NUMBER             number            NOT NULL,
                IS_VALID               NUMBER(1, 0) DEFAULT 1,
                ADMISSION_DATE         DATE              NOT NULL,
                ADMISSION_EDITOR_ID    VARCHAR(128)      NOT NULL,
                MODIFICATION_DATE      DATE,
                MODIFICATION_EDITOR_ID VARCHAR(128),
                UNIQUE (OFFICE_NUMBER, KIND, YEAR)
            );
        """.trimIndent()

        val expectedSql = """
            CREATE TABLE TEST_TABLE
            (
                ID                     VARCHAR(128)      NOT NULL PRIMARY KEY,
                OFFICE_NUMBER          VARCHAR2(2 CHAR)  NOT NULL,
                KIND                   VARCHAR2(32 CHAR) NOT NULL,
                YEAR                   NUMBER            NOT NULL,
                MAX_NUMBER             number            NOT NULL,
                IS_VALID               NUMBER(1, 0) DEFAULT 1,
                ADMISSION_DATE         DATE              NOT NULL,
                ADMISSION_EDITOR_ID    VARCHAR(128)      NOT NULL,
                MODIFICATION_DATE      DATE,
                MODIFICATION_EDITOR_ID VARCHAR(128),
                UNIQUE (OFFICE_NUMBER, KIND, YEAR)
            );
        """.trimIndent()

        val processedSql = PreProcessor.preProcessSql(sqlForTesting)

        assertEquals(expectedSql, processedSql)
    }

    @Test
    fun `passing in an sql statement that contains mixed comments and also commented out code`() {
        val sqlForTesting = """
            -- this is the start of crazy
            CREATE TABLE TEST_TABLE -- first inline comment
            (
            -- this is the start of crazy
                ID                     VARCHAR(128)      NOT NULL PRIMARY KEY, -- yet another inline comment
                OFFICE_NUMBER          VARCHAR2(2 CHAR)  NOT NULL,
                -- KIND                   VARCHAR2(32 CHAR) NOT NULL,
                YEAR                   NUMBER            , -- NOT NULL,
                MAX_NUMBER             number            NOT NULL,
                IS_VALID               NUMBER(1, 0) DEFAULT 1,
                ADMISSION_DATE         DATE              NOT NULL,
                ADMISSION_EDITOR_ID    VARCHAR(128)      NOT NULL,
                MODIFICATION_DATE      DATE,
                MODIFICATION_EDITOR_ID VARCHAR(128),
                UNIQUE (OFFICE_NUMBER, KIND, YEAR)
                -- this is crazy
            );
        """.trimIndent()

        val expectedSql = """
            CREATE TABLE TEST_TABLE
            (
                ID                     VARCHAR(128)      NOT NULL PRIMARY KEY,
                OFFICE_NUMBER          VARCHAR2(2 CHAR)  NOT NULL,
                YEAR                   NUMBER            ,
                MAX_NUMBER             number            NOT NULL,
                IS_VALID               NUMBER(1, 0) DEFAULT 1,
                ADMISSION_DATE         DATE              NOT NULL,
                ADMISSION_EDITOR_ID    VARCHAR(128)      NOT NULL,
                MODIFICATION_DATE      DATE,
                MODIFICATION_EDITOR_ID VARCHAR(128),
                UNIQUE (OFFICE_NUMBER, KIND, YEAR)
            );
        """.trimIndent()

        val processedSql = PreProcessor.preProcessSql(sqlForTesting)

        assertEquals(expectedSql, processedSql)
    }
}
