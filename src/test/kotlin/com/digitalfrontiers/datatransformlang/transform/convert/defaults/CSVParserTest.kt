package com.digitalfrontiers.datatransformlang.transform.convert.defaults

import com.fasterxml.jackson.dataformat.csv.CsvReadException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CSVParserTest {

    private val parser = CSVParser()

    private val csvString = """
            firstname,lastname,age
            barack,obama,63
            Cristiano,Ronaldo,39
    """.trimIndent()

    private val tooManyEntriesString = """
            firstname,lastname,age
            barack,obama,63,1,2,3,4,5
    """.trimIndent()

    @Test
    fun `test simple example`() {
        assertEquals(
            "[{firstname=barack, lastname=obama, age=63}, {firstname=Cristiano, lastname=Ronaldo, age=39}]",
            parser.parse(csvString).toString(),
            message = "Convert both to String for deep content comparison. Does not work out of the box without."
        )
    }

    @Test
    fun `throws CsvReadException on empty CSV content`() {
        val csvString = ""

        assertThrows<CsvReadException> { parser.parse(csvString) }
    }

    @Test
    fun `throws CsvReadException on too many entries`() {
        assertThrows<CsvReadException> { parser.parse(tooManyEntriesString) }
    }
}
