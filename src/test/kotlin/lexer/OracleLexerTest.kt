package lexer

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OracleLexerTest {

    @Test
    fun `create table`() {
        val testString = "CREATE TABLE Test\n"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.CreateKeyword, tokens.first())
        assertEquals(Token.TableKeyword, tokens[1])
        assertEquals(Token.Identifier("test"), tokens[2])
        assertEquals(3, tokens.size)
    }

    @Test
    fun `Not Null consume`() {
        val testString = "NOT NULL\n"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.NotNull, tokens.first())
        assertEquals(1, tokens.size)
    }

    @Test
    fun `Not Null consume mixed case`() {
        val testString = "Not nUlL\n"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.NotNull, tokens.first())
        assertEquals(1, tokens.size)
    }

    @Test
    fun `primary key statement`() {
        val testString = "PRIMARY KEY,"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.PrimaryKey, tokens.first())
        assertEquals(1, tokens.size)
    }

    @Test
    fun `primary key statement followed by whitespace`() {
        val testString = "PRIMARY KEY "
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.PrimaryKey, tokens.first())
        assertEquals(1, tokens.size)
    }

    @Test
    fun `full unique statement`() {
        val testString = "UNIQUE (OFFICE_NUMBER, KIND, YEAR)\n"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Unique, tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("office_number"), tokens[2])
        assertEquals(Token.Identifier("kind"), tokens[3])
        assertEquals(Token.Identifier("year"), tokens[4])
        assertEquals(Token.CloseBracket, tokens[5])
        assertEquals(6, tokens.size)
    }

    @Test
    fun `full unique statement without whitespace before open bracket`() {
        val testString = "UNIQUE(OFFICE_NUMBER, KIND, YEAR)\n"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Unique, tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("office_number"), tokens[2])
        assertEquals(Token.Identifier("kind"), tokens[3])
        assertEquals(Token.Identifier("year"), tokens[4])
        assertEquals(Token.CloseBracket, tokens[5])
        assertEquals(6, tokens.size)
    }

    @Test
    fun `full unique statement without whitespaces between the columns`() {
        val testString = "UNIQUE(OFFICE_NUMBER,KIND,YEAR)\n"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Unique, tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("office_number"), tokens[2])
        assertEquals(Token.Identifier("kind"), tokens[3])
        assertEquals(Token.Identifier("year"), tokens[4])
        assertEquals(Token.CloseBracket, tokens[5])
        assertEquals(6, tokens.size)
    }

    @Test
    fun `full unique statement close bracket at the end`() {
        val testString = "UNIQUE(OFFICE_NUMBER,KIND,YEAR))"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Unique, tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("office_number"), tokens[2])
        assertEquals(Token.Identifier("kind"), tokens[3])
        assertEquals(Token.Identifier("year"), tokens[4])
        assertEquals(Token.CloseBracket, tokens[5])
        assertEquals(Token.CloseBracket, tokens[6])
        assertEquals(7, tokens.size)
    }

    @Test
    fun `full unique statement only one column`() {
        val testString = "UNIQUE(OFFICE_NUMBER))"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Unique, tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("office_number"), tokens[2])
        assertEquals(Token.CloseBracket, tokens[3])
        assertEquals(Token.CloseBracket, tokens[4])
        assertEquals(5, tokens.size)
    }

    @Test
    fun `varchar with only number inside brackets`() {
        val testString = "VARCHAR(128) "
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("varchar"), tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("128"), tokens[2])
        assertEquals(Token.CloseBracket, tokens[3])
        assertEquals(4, tokens.size)
    }

    @Test
    fun `varchar with only number inside brackets but whitespace`() {
        val testString = "VARCHAR (128) "
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("varchar"), tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("128"), tokens[2])
        assertEquals(Token.CloseBracket, tokens[3])
        assertEquals(4, tokens.size)
    }

    @Test
    fun `varchar with only number inside brackets but whitespace in brackets`() {
        val testString = "VARCHAR (128 ),"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("varchar"), tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("128"), tokens[2])
        assertEquals(Token.CloseBracket, tokens[3])
        assertEquals(4, tokens.size)
    }

    @Test
    fun `varchar2 with number and string in brackets`(){
        val testString = "VARCHAR2(32 CHAR) "
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("varchar2"), tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("32"), tokens[2])
        assertEquals(Token.Identifier("char"), tokens[3])
        assertEquals(Token.CloseBracket, tokens[4])
        assertEquals(5, tokens.size)
    }

    @Test
    fun `varchar2 with number and string in brackets with whitespace before brackets`(){
        val testString = "VARCHAR2 (32 CHAR) "
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("varchar2"), tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("32"), tokens[2])
        assertEquals(Token.Identifier("char"), tokens[3])
        assertEquals(Token.CloseBracket, tokens[4])
        assertEquals(5, tokens.size)
    }

    @Test
    fun `number without brackets`(){
        val testString = "NUMBER "
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("number"), tokens.first())
        assertEquals(1, tokens.size)
    }

    @Test
    fun `number without brackets comma at the end`(){
        val testString = "NUMBER,"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("number"), tokens.first())
        assertEquals(1, tokens.size)
    }

    @Test
    fun `number without brackets in lower case`(){
        val testString = "number "
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("number"), tokens.first())
        assertEquals(1, tokens.size)
    }

    @Test
    fun `number with brackets and two numbers in it`(){
        val testString = "NUMBER(1,0) "
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("number"), tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("1"), tokens[2])
        assertEquals(Token.Identifier("0"), tokens[3])
        assertEquals(Token.CloseBracket, tokens[4])
        assertEquals(5, tokens.size)
    }

    @Test
    fun `number with brackets and two numbers and whitespace in it`(){
        val testString = "NUMBER(1 , 0)"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("number"), tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("1"), tokens[2])
        assertEquals(Token.Identifier("0"), tokens[3])
        assertEquals(Token.CloseBracket, tokens[4])
        assertEquals(5, tokens.size)
    }

    @Test
    fun `number with brackets and two numbers and comma at the end`(){
        val testString = "NUMBER(1,0),"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("number"), tokens.first())
        assertEquals(Token.OpenBracket, tokens[1])
        assertEquals(Token.Identifier("1"), tokens[2])
        assertEquals(Token.Identifier("0"), tokens[3])
        assertEquals(Token.CloseBracket, tokens[4])
        assertEquals(5, tokens.size)
    }

    @Test
    fun `date with whitespace at the end`(){
        val testString = "DATE "
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("date"), tokens.first())
        assertEquals(1, tokens.size)
    }

    @Test
    fun `date with comma at the end`(){
        val testString = "DATE,"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Identifier("date"), tokens.first())
        assertEquals(1, tokens.size)
    }

    @Test
    fun `default with number and comma at the end`(){
        val testString = "DEFAULT 1,"
        val tokens = OracleLexer().generateTokens(testString)

        assertEquals(Token.Default, tokens.first())
        assertEquals(Token.Identifier("1"), tokens[1])
        assertEquals(2, tokens.size)
    }
}