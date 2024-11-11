package com.digitalfrontiers.datatransformlang.transform.convert.defaults

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class YAMLParserTest {

    private val yamlParser = YAMLParser()

    @Test
    fun `test simple example`() {

        val input = """
            ---  # Document start marker
            
            string_value: "Hello, YAML!"
            unquoted_string: Hello, YAML!
            number: 42
            
        """.trimIndent()

        val x: Map<String, Any> = yamlParser.parse(input) as Map<String, Any>

        assertEquals("Hello, YAML!", x["string_value"])
        assertEquals("Hello, YAML!", x["unquoted_string"])
        assertEquals(42, x["number"])
    }

    @Test
    fun `test empty input`() {

        assertThrows<MismatchedInputException> {
            yamlParser.parse("")
        }
    }
}