package com.digitalfrontiers.datatransformlang.util.io

import com.digitalfrontiers.datatransformlang.transform.Const
import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.transform.ToArray
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.fasterxml.jackson.databind.ObjectMapper

class TransformsKtTest {
    private val mapper = ObjectMapper()

    @Test
    fun `test parse const`() {
        val json = """
            {
                "type": "Const",
                "value": "test-value"
            }
        """.trimIndent()
        val node = mapper.readTree(json)

        val result = parseTransformNode(node)

        assertTrue(result is Const)
        assertEquals(""" "test-value" """.trim(), (result as Const).value.toString().trim())
    }

    @Test
    fun `test parse fetch transform`() {
        val json = """
            {
                "type": "Fetch",
                "path": "data.user.name"
            }
        """.trimIndent()
        val node = mapper.readTree(json)

        val result = parseTransformNode(node)

        assertTrue(result is Specification.Fetch)
        assertEquals("data.user.name", (result as Specification.Fetch).path)
    }

    @Test
    fun `test parse toArray transform`() {
        val json = """
            {
                "type": "ToArray",
                "items": [
                    {
                        "type": "Const",
                        "value": 1
                    },
                    {
                        "type": "Const",
                        "value": 2
                    }
                ]
            }
        """.trimIndent()
        val node = mapper.readTree(json)

        val result = parseTransformNode(node)

        assertTrue(result is ToArray)
        assertEquals(2, (result as ToArray).items.size)
        assertTrue(result.items[0] is Const)
        assertTrue(result.items[1] is Const)
    }

    @Test
    fun `test parse invalid transform type`() {
        val json = """
            {
                "type": "InvalidType",
                "value": "test"
            }
        """.trimIndent()
        val node = mapper.readTree(json)

        assertThrows(IllegalArgumentException::class.java) {
            parseTransformNode(node)
        }
    }
}