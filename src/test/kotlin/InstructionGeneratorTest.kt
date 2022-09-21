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
        assertEquals(Instruction.Obj("test_table", "test_table"), instructions.elementAt(0))
        assertEquals(Instruction.ColStmt("id", "v128",false, null), instructions.elementAt(1))
        assertEquals(Instruction.ColStmt("kind", "v32",false, null), instructions.elementAt(2))
        assertEquals(Instruction.ColStmt("year", "n",true, null), instructions.elementAt(3))
        assertEquals(Instruction.ColStmt("this_id", "n",false, null), instructions.elementAt(4))
        assertEquals(Instruction.ColStmt("is_valid", "p1;0",true, 1), instructions.elementAt(5))
        assertEquals(Instruction.ColStmt("admission_date", "d",false, null), instructions.elementAt(6))
        assertEquals(Instruction.Pk("id"), instructions.elementAt(7))
        assertEquals(Instruction.Unique(listOf("this_id")), instructions.elementAt(8))
        assertEquals(Instruction.Fk("fk_other_id", "this_id", "other_table", "other_id"), instructions.elementAt(9))
        assertEquals(Instruction.Finalize, instructions.elementAt(10))
    }
}