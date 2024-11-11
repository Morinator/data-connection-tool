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
    fun testToConstTransform() {
        val constTransform = Const(42)

        val result = applyTransform(emptyMap, constTransform)

        assertEquals(42, result)
    }

    @Test
    fun testToInputTransform() {
        val jsonDocument = mapOf("name" to "josh")
        val inputTransform = Input("$.name")

        val result = applyTransform(jsonDocument, inputTransform)

        assertEquals("josh", result)
    }

    @Test
    fun testToArrayTransform() {
        val arrayTransform = Array(
            Const(1),
            Const(2),
            Const(3)
        )

        val result = applyTransform(emptyMap, arrayTransform)

        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun testToObjectTransform() {
        val objectTransform = Object {
            "a" to 1
            "b" to 2
        }

        val result = applyTransform(emptyMap, objectTransform)

        assertEquals(mapOf("a" to 1, "b" to 2), result)
    }

    @Test
    fun testForEachTransform() {
        val listOfTransform = ListOf { Const(42) }
        val data: Data = listOf(1, 2, 3)

        val result = applyTransform(data, listOfTransform)

        assertEquals(listOf(42, 42, 42), result)
    }

    @Test
    fun testExtendTransform() {
        val extensionTransform = Extension {
            "c" to 3
            "d" to 4
        }

        val result = applyTransform(mapOf("a" to 1, "b" to 2), extensionTransform)

        assertEquals(mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4), result)
    }

    @Test
    fun testCallTransform() {
        // Register a dummy function

        val sum: CustomFunction = {
                args: List<Any?> -> (args[0] as Int) + (args[1] as Int)
        }

//        registerFunction("sum") {
//            args: List<Any?> -> (args[0] as Int) + (args[1] as Int)
//        }

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