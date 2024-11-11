package com.digitalfrontiers.datatransformlang.transform.convert.defaults

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class YAMLParserTest {

    private val yamlParser = YAMLParser()

    @Test
    fun `test different data types`() {

        val input = """
            ---  # Document start marker
            
            string_value: "Hello, YAML!"
            unquoted_string: Hello, YAML!
            number: 42
            my_bool: true
            
        """.trimIndent()

        val x: Map<String, Any> = yamlParser.parse(input) as Map<String, Any>

        assertEquals("Hello, YAML!", x["string_value"])
        assertEquals("Hello, YAML!", x["unquoted_string"])
        assertEquals(42, x["number"])
        assertEquals(true, x["my_bool"])
    }

    @Test
    fun `empty input produces null`() {

        // also prints stacktrace for com.fasterxml.jackson.databind.exc.MismatchedInputException
        assertEquals(null, yamlParser.parse(""))
    }

    @Test
    fun `test simple list`() {

        val input = """
            - apple
            - banana
            - orange
        """.trimIndent()

        assertEquals(listOf("apple", "banana", "orange"), yamlParser.parse(input))
    }
}