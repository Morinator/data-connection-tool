package com.digitalfrontiers.util

import com.digitalfrontiers.persistence.SpecificationJsonConfig
import com.digitalfrontiers.services.JsonService
import com.digitalfrontiers.transform.*
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TransformNodeParserTest @Autowired constructor(
    val jsonService: JsonService
) {


    @Test
    fun `test Self type`() {
        val json = """{ "type": "Self" }"""
        val result = jsonService.parseJsonString(json)
        assertEquals(Self, result)
    }

    @Test
    fun `test Const type`() {
        val json = """{ "type": "Const", "value": 42 }"""
        val result = jsonService.parseJsonString(json)
        assertTrue(result is Const)
        assertEquals(42, (result as Const).value)
    }

    @Test
    fun `test Input type`() {
        val json = """{ "type": "Input", "path": "user.name" }"""
        val result = jsonService.parseJsonString(json)
        assertTrue(result is Input)
        assertEquals("user.name", (result as Input).path)
    }

    @Test
    fun `test Tuple type`() {
        val json = """{
            "type": "Tuple",
            "items": [
                { "type": "Const", "value": "hello" },
                { "type": "Self" }
            ]
        }"""
        val result = jsonService.parseJsonString(json)
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
            "type": "Record",
            "entries": {
                "key1": { "type": "Const", "value": 123 },
                "key2": { "type": "Self" }
            }
        }"""
        val result = jsonService.parseJsonString(json)
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
            "type": "ListOf",
            "mapping": { "type": "Const", "value": "mapped" }
        }"""
        val result = jsonService.parseJsonString(json)
        assertTrue(result is ListOf)
        val mapping = (result as ListOf).mapping
        assertTrue(mapping is Const)
        assertEquals("mapped", (mapping as Const).value)
    }

    @Test
    fun `test ResultOf type`() {
        val json = """{
            "type": "ResultOf",
            "fid": "functionId",
            "args": [
                { "type": "Const", "value": 10 },
                { "type": "Self" }
            ]
        }"""
        val result = jsonService.parseJsonString(json)
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
            "type": "Compose",
            "steps": [
                { "type": "Const", "value": "step1" },
                { "type": "Self" }
            ]
        }"""
        val result = jsonService.parseJsonString(json)
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
            jsonService.parseJsonString(json)
        }
        println(exception.message)
    }

    @Test
    fun `the type field is missing`() {
        val specString = """{"entries":{"x":{"path":"a"},"y":{"path":"b"}}}"""
        val exception = assertThrows<InvalidTypeIdException> {
            jsonService.parseJsonString(specString)
        }
        println(exception)
    }

    @Test
    fun `simple test case with type field`() {
        val specString = """{"type":"Record","entries":{"x":{"type":"Input","path":"a"},"y":{"type":"Input","path":"b"}}}"""
        val x = jsonService.parseJsonString(specString)
        println(x)
    }

    @Test
    fun `test spec serialization`() {
        val spec = Specification.Record {
            "a" from "b"
            "y" to Specification.Const("const123")
        }

        val str = SpecificationJsonConfig.createMapper().writeValueAsString(spec)
        assertEquals(
            """{"type":"Record","entries":{"a":{"type":"Input","path":"b"},"y":{"type":"Const","value":"const123"}}}""",
            str

        )
    }
}
