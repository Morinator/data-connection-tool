package com.digitalfrontiers.dataconnectiontool.util

import com.digitalfrontiers.datatransformlang.transform.Const
import com.digitalfrontiers.datatransformlang.transform.Fetch
import com.digitalfrontiers.datatransformlang.transform.ToArray
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

        assertTrue(result is Const)
        assertEquals("abc", (result as Const).value)
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

        assertTrue(result is Fetch)
        assertEquals("data.user.name", (result as Fetch).path)
    }

    @Test
    fun `test parse toArray transform`() {
        val json = """
            {
                "type": "ToArray",
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

        assertTrue(result is ToArray)
        assertEquals(3, (result as ToArray).items.size)

        assertTrue(result.items[0] is Const)
        assertTrue(result.items[1] is Const)
        assertTrue(result.items[2] is Const)

        assertEquals(7, (result.items[0] as Const).value)
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