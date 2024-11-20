package com.digitalfrontiers.util

import com.digitalfrontiers.transform.Specification
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TransformsKtTest {
    private val mapper = ObjectMapper()

    @Test
    fun `test parse const`() {
        val json = """
            {
                "type": "Const",
                "value": "abc"
            }
        """.trimIndent()
        val node = mapper.readTree(json)

        val result = parseTransformNode(node)

        assertTrue(result is Specification.Const)
        assertEquals("abc", (result as Specification.Const).value)
    }

    @Test
    fun `test parse fetch transform`() {
        val json = """
            {
                "type": "Input",
                "path": "data.user.name"
            }
        """.trimIndent()
        val node = mapper.readTree(json)

        val result = parseTransformNode(node)

        assertTrue(result is Specification.Input)
        assertEquals("data.user.name", (result as Specification.Input).path)
    }

    @Test
    fun `test parse toArray transform`() {
        val json = """
            {
                "type": "Tuple",
                "items": [
                    {
                        "type": "Const",
                        "value": 7
                    },
                    {
                        "type": "Const",
                        "value": 8
                    },
                    {
                        "type": "Const",
                        "value": 9
                    }
                ]
            }
        """.trimIndent()
        val node = mapper.readTree(json)

        val result = parseTransformNode(node)

        assertTrue(result is Specification.Tuple)
        assertEquals(3, (result as Specification.Tuple).items.size)

        assertTrue(result.items[0] is Specification.Const)
        assertTrue(result.items[1] is Specification.Const)
        assertTrue(result.items[2] is Specification.Const)

        assertEquals(7, (result.items[0] as Specification.Const).value)
    }

    @Test
    fun `test parse invalid transform type`() {
        val json = """
            {
                "type": "InvalidType",
                "value": "abc"
            }
        """.trimIndent()
        val node = mapper.readTree(json)

        assertThrows(IllegalArgumentException::class.java) {
            parseTransformNode(node)
        }
    }
}