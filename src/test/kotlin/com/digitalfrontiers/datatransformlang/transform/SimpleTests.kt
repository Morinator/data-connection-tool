package com.digitalfrontiers.datatransformlang.transform

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * A brief test for each type Specification
 */
class SimpleTests {

    private val emptyMap: Data = emptyMap<String, Any>()

    @Test
    fun testToConstTransform() {
        val toConstTransform = ToConst(42)

        val result = applyTransform(emptyMap, toConstTransform)

        assertEquals(42, result)
    }

    @Test
    fun testToInputTransform() {
        val jsonDocument = mapOf("name" to "josh")
        val toInputTransform = ToInput("$.name")

        val result = applyTransform(jsonDocument, toInputTransform)

        assertEquals("josh", result)
    }

    @Test
    fun testToArrayTransform() {
        val toArrayTransform = ToArray(
            ToConst(1),
            ToConst(2),
            ToConst(3)
        )

        val result = applyTransform(emptyMap, toArrayTransform)

        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun testToObjectTransform() {
        val toObjectTransform = ToObject {
            "a" to 1
            "b" to 2
        }

        val result = applyTransform(emptyMap, toObjectTransform)

        assertEquals(mapOf("a" to 1, "b" to 2), result)
    }

    @Test
    fun testForEachTransform() {
        val forEachTransform = ForEach { ToConst(42) }
        val data: Data = listOf(1, 2, 3)

        val result = applyTransform(data, forEachTransform)

        assertEquals(listOf(42, 42, 42), result)
    }

    @Test
    fun testExtendTransform() {
        val extendTransform = Extend {
            "c" to 3
            "d" to 4
        }

        val result = applyTransform(mapOf("a" to 1, "b" to 2), extendTransform)

        assertEquals(mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4), result)
    }

    @Test
    fun testCallTransform() {
        // Register a dummy function
        registerFunction("sum") {
            args: List<Any> -> (args[0] as Int) + (args[1] as Int)
        }

        val callTransform = Call {
            "sum"(5, 10)
        }

        val result = applyTransform(emptyMap, callTransform)

        assertEquals(15, result)
    }

    @Test
    fun testComposeTransform() {
        val composeTransform = Compose(
            ToConst(1),
            ToConst(2),
            ToConst(3)
        )

        val result = applyTransform(emptyMap, composeTransform)

        assertEquals(3, result)  // The last transformation step should be the result
    }

}