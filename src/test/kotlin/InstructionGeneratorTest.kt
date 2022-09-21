import lexer.OracleLexer
import org.junit.jupiter.api.Test
import parser.OracleParser
import kotlin.test.assertEquals

/*
 * Copyright (c) 2022, Patrick Wilmes <patrick.wilmes@bit-lake.com>
 * Copyright (c) 2022, Christoph Helbing <manig.christoph@googlemail.com>
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
class InstructionGeneratorTest {
    @Test
    fun `generate instructions for full table statement`(){
        val sqlString = """
            CREATE TABLE TEST_TABLE
            (
                ID              VARCHAR(128)        NOT NULL PRIMARY KEY,
                KIND            VARCHAR2(32 CHAR)   NOT NULL,
                YEAR            NUMBER,
                THIS_ID         NUMBER              NOT NULL,
                IS_VALID        NUMBER(1, 0)        DEFAULT 1,
                ADMISSION_DATE  DATE                NOT NULL,
                UNIQUE (THIS_ID),                
                CONSTRAINT fk_other_id FOREIGN KEY (THIS_ID) REFERENCES OTHER_TABLE (OTHER_ID)
            );
        """.trimIndent()
        val tokens = OracleLexer().generateTokens(sqlString)
        val script = OracleParser().parse(tokens)
        val instructions = generateInstructionsForScript(script)

        assertEquals(11, instructions.size)
        assertEquals(Instruction.Obj("Test_table", "test_table"), instructions.elementAt(0))
        assertEquals(Instruction.ColStmt("Id", "v128",false, null), instructions.elementAt(1))
        assertEquals(Instruction.ColStmt("Kind", "v32",false, null), instructions.elementAt(2))
        assertEquals(Instruction.ColStmt("Year", "n",true, null), instructions.elementAt(3))
        assertEquals(Instruction.ColStmt("This_id", "n",false, null), instructions.elementAt(4))
        assertEquals(Instruction.ColStmt("Is_valid", "p1;0",true, 1), instructions.elementAt(5))
        assertEquals(Instruction.ColStmt("Admission_date", "d",false, null), instructions.elementAt(6))
        assertEquals(Instruction.Pk("Id"), instructions.elementAt(7))
        assertEquals(Instruction.Unique(listOf("This_id")), instructions.elementAt(8))
        assertEquals(Instruction.Fk("fk_other_id", "This_id", "Other_table", "Other_id"), instructions.elementAt(9))
        assertEquals(Instruction.Finalize, instructions.elementAt(10))
    }
}