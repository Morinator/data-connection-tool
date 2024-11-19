package com.digitalfrontiers.transform.convert.defaults

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class YAMLSerializerTest {

    private val yamlSerializer = YAMLSerializer()

    @Test
    fun `test different data types`() {

        val expected = """
            ---
            string_value: "Hello, YAML!"
            number: 42
            my_bool: true
            
        """.trimIndent()

        val input = mapOf(
            "string_value" to "Hello, YAML!",
            "number" to 42,
            "my_bool" to true,
        )
        val x = yamlSerializer.serialize(input)
        assertEquals(expected, x)
    }

    @Test
    fun `empty input produces null`() {

        val expected = """
            ---
            - 1
            - 3
            - 5
            - 7

        """.trimIndent()
        assertEquals(expected, yamlSerializer.serialize(listOf(1,3,5,7)))
    }
}