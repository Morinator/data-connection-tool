package com.digitalfrontiers.dataconnectiontool.util

import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class JsonUtilsTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun `test null` () {
        assertNull(JsonUtils.unbox(null))
    }

    @Test
    fun `test IntNode` () {
        assertEquals(3, JsonUtils.unbox(IntNode.valueOf(3)))
    }

    @Test
    fun `test TextNode` () {
        assertEquals("hi", JsonUtils.unbox(TextNode.valueOf("hi")))
    }
    @Test
    fun `test BooleanNode` () {
        assertEquals(true, JsonUtils.unbox(BooleanNode.valueOf(true)))
    }

    @Test
    fun `test map with different values`() {

        // given
        val jsonString = """
            {
                "number": 42,
                "decimal": 3.14,
                "text": "hello",
                "boolean": true,
                "null": null,
                "array": [1, "two", 3.0, null],
                "object": {
                    "nested": "value"
                }
            }
        """.trimIndent()
        val jsonNode = mapper.readTree(jsonString)

        // when
        val unboxed = JsonUtils.unbox(jsonNode) as Map<*, *>

        // then
        assertEquals(42, unboxed["number"])
        assertEquals(3.14, unboxed["decimal"])
        assertEquals("hello", unboxed["text"])
        assertEquals(true, unboxed["boolean"])
        assertEquals(null, unboxed["null"])

        assertEquals(
            listOf(1, "two", 3.0, null),
            unboxed["array"] as List<*>
        )

        val nestedObject = unboxed["object"] as Map<*, *>
        assertEquals("value", nestedObject["nested"])
    }


}