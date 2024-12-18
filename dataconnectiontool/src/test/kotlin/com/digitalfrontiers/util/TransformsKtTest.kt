package com.digitalfrontiers.util

import com.digitalfrontiers.transform.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TransformNodeParserTest @Autowired constructor(
    private val objectMapper: ObjectMapper
) {
    
    private fun stringToTransformation(jsonString: String): Transformation {
        return objectMapper.readValue(jsonString, Transformation::class.java)
    }

    private fun transformationToJson(transformation: Transformation): String {
        return objectMapper.writeValueAsString(transformation)
    }

    @Test
    fun `test Self type`() {
        val json = """{ "type": "self" }"""
        val result = stringToTransformation(json)
        assertEquals(Self, result)
    }

    @Test
    fun `test Const type`() {
        val json = """{ "type": "const", "value": 42 }"""
        val result = stringToTransformation(json)
        assertTrue(result is Const)
        assertEquals(42, (result as Const).value)
    }

    @Test
    fun `test Input type`() {
        val json = """{ "type": "input", "path": "user.name" }"""
        val result = stringToTransformation(json)
        assertTrue(result is Input)
        assertEquals("user.name", (result as Input).path)
    }

    @Test
    fun `test Tuple type`() {
        val json = """{
            "type": "tuple",
            "items": [
                { "type": "const", "value": "hello" },
                { "type": "self" }
            ]
        }"""
        val result = stringToTransformation(json)
        assertTrue(result is Tuple)
        val items = (result as Tuple).items
        assertEquals(2, items.size)
        assertTrue(items[0] is Const)
        assertEquals("hello", (items[0] as Const).value)
        assertEquals(Self, items[1])
    }

    @Test
    fun `test Record type`() {
        val json = """{
            "type": "record",
            "entries": {
                "key1": { "type": "const", "value": 123 },
                "key2": { "type": "self" }
            }
        }"""
        val result = stringToTransformation(json)
        assertTrue(result is Record)
        val entries = (result as Record).entries
        assertEquals(2, entries.size)
        assertTrue(entries["key1"] is Const)
        assertEquals(123, (entries["key1"] as Const).value)
        assertEquals(Self, entries["key2"])
    }

    @Test
    fun `test ListOf type`() {
        val json = """{
            "type": "listOf",
            "mapping": { "type": "const", "value": "mapped" }
        }"""
        val result = stringToTransformation(json)
        assertTrue(result is ListOf)
        val mapping = (result as ListOf).mapping
        assertTrue(mapping is Const)
        assertEquals("mapped", (mapping as Const).value)
    }

    @Test
    fun `test ResultOf type`() {
        val json = """{
            "type": "resultOf",
            "fid": "functionId",
            "args": [
                { "type": "const", "value": 10 },
                { "type": "self" }
            ]
        }"""
        val result = stringToTransformation(json)
        assertTrue(result is ResultOf)
        val fid = (result as ResultOf).fid
        val args = result.args
        assertEquals("functionId", fid)
        assertEquals(2, args.size)
        assertTrue(args[0] is Const)
        assertEquals(10, (args[0] as Const).value)
        assertEquals(Self, args[1])
    }

    @Test
    fun `test Compose type`() {
        val json = """{
            "type": "compose",
            "steps": [
                { "type": "const", "value": "step1" },
                { "type": "self" }
            ]
        }"""
        val result = stringToTransformation(json)
        assertTrue(result is Compose)
        val steps = (result as Compose).steps
        assertEquals(2, steps.size)
        assertTrue(steps[0] is Const)
        assertEquals("step1", (steps[0] as Const).value)
        assertEquals(Self, steps[1])
    }

    @Test
    fun `test unknown type`() {
        val json = """{ "type": "UnknownType" }"""
        val exception = assertThrows<InvalidTypeIdException> {
            stringToTransformation(json)
        }
        println(exception.message)
    }

    @Test
    fun `the type field is missing`() {
        val specString = """{"entries":{"x":{"path":"a"},"y":{"path":"b"}}}"""
        val exception = assertThrows<InvalidTypeIdException> {
            stringToTransformation(specString)
        }
        println(exception)
    }

    @Test
    fun `simple test case with type field`() {
        val specString = """{"type":"record","entries":{"x":{"type":"input","path":"a"},"y":{"type":"input","path":"b"}}}"""
        val x = stringToTransformation(specString)
        println(x)
    }

    @Test
    fun `test spec serialization`() {
        val spec = Transformation.Record {
            "a" from "b"
            "y" to Transformation.Const("const123")
        }

        val str = transformationToJson(spec)
        assertEquals(
            """{"type":"record","entries":{"a":{"type":"input","path":"b"},"y":{"type":"const","value":"const123"}}}""",
            str

        )
    }
}
