package com.digitalfrontiers.datatransformlang.transform

import com.digitalfrontiers.datatransformlang.CustomFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * A brief test for each type Specification
 */
class SimpleTests {

    private val emptyMap: Data = emptyMap<String, Any>()

    @Test
    fun testConstTransform() {
        val constTransform = Const(42)

        val result = applyTransform(emptyMap, constTransform)

        assertEquals(42, result)
    }

    @Test
    fun testInputTransform() {
        val jsonDocument = mapOf("name" to "josh")
        val inputTransform = Input("$.name")

        val result = applyTransform(jsonDocument, inputTransform)

        assertEquals("josh", result)
    }

    @Test
    fun testArrayTransform() {
        val arrayTransform = Array(
            Const(1),
            Const(2),
            Const(3)
        )

        val result = applyTransform(emptyMap, arrayTransform)

        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun testObjectTransform() {
        val objectTransform = Object {
            "a" to 1
            "b" to 2
        }

        val result = applyTransform(emptyMap, objectTransform)

        assertEquals(mapOf("a" to 1, "b" to 2), result)
    }

    @Test
    fun testListOfTransform() {
        val listOfTransform = ListOf { Const(42) }
        val data: Data = listOf(1, 2, 3)

        val result = applyTransform(data, listOfTransform)

        assertEquals(listOf(42, 42, 42), result)
    }

    @Test
    fun testExtensionTransform() {
        val extensionTransform = Extension {
            "c" to 3
            "d" to 4
        }
        val result = applyTransform(mapOf("a" to 1, "b" to 2), extensionTransform)

        assertEquals(mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4), result)
    }

    @Test
    fun testRemapTransform() {
        val data = mapOf(
            "a" to 1,
            "b" to 2
        )

        val remapWithPairs = Specification.Remap.WithPairs(mapOf("a" to "x", "b" to "y"))

        val firstResult = applyTransform(data, remapWithPairs)

        assertEquals(mapOf("x" to 1, "y" to 2), firstResult)

        val remapWithFunc = Specification.Remap.WithFunc { it.uppercase() }

        val secondResult = applyTransform(data, remapWithFunc)

        assertEquals(mapOf("A" to 1, "B" to 2), secondResult)
    }

    @Test
    fun testResultOfTransform() {
        // Register a dummy function

        val sum: CustomFunction = {
                args: List<Any?> -> (args[0] as Int) + (args[1] as Int)
        }

        val resultOfTransform = ResultOf {
            "sum"(5, 10)
        }

        val result = applyTransform(
            emptyMap,
            resultOfTransform,
            mapOf("sum" to sum)
        )

        assertEquals(15, result)
    }

    @Test
    fun testComposeTransform() {
        val composeTransform = Compose {
            Const(1) then
            Const(2) then
            Const(3)
        }

        val result = applyTransform(emptyMap, composeTransform)

        assertEquals(3, result)  // The last transformation step should be the result
    }

}