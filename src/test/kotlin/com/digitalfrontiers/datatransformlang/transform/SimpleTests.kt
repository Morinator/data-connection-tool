package com.digitalfrontiers.datatransformlang.transform

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
    fun testFetchTransform() {
        val jsonDocument = mapOf("name" to "josh")
        val fetchTransform = Fetch("$.name")

        val result = applyTransform(jsonDocument, fetchTransform)

        assertEquals("josh", result)
    }

    @Test
    fun testToArrayTransform() {
        val toArrayTransform = ToArray(
            Const(1),
            Const(2),
            Const(3)
        )

        val result = applyTransform(emptyMap, toArrayTransform)

        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun testToObjectTransform() {
        val toObjectTransform = ToObject(
            "a" to Const(1),
            "b" to Const(2)
        )

        val result = applyTransform(emptyMap, toObjectTransform)

        assertEquals(mapOf("a" to 1, "b" to 2), result)
    }

    @Test
    fun testForEachTransform() {
        val forEachTransform = ForEach(Const(42))
        val data: Data = listOf(1, 2, 3)

        val result = applyTransform(data, forEachTransform)

        assertEquals(listOf(42, 42, 42), result)
    }

    @Test
    fun testCallTransform() {
        // Register a dummy function
        registerFunction("sum") {
            args: List<Any> -> (args[0] as Int) + (args[1] as Int)
        }

        val callTransform = Call("sum", Const(5), Const(10))

        val result = applyTransform(emptyMap, callTransform)

        assertEquals(15, result)
    }

    @Test
    fun testComposeTransform() {
        val composeTransform = Compose(
            Const(1),
            Const(2),
            Const(3)
        )

        val result = applyTransform(emptyMap, composeTransform)

        assertEquals(3, result)  // The last transformation step should be the result
    }

}