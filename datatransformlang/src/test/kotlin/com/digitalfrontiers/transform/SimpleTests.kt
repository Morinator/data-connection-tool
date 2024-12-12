package com.digitalfrontiers.transform

import com.digitalfrontiers.CustomFunction
import com.digitalfrontiers.transform.Transformation.Rename
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
        val tupleTransform = Tuple(
            Const(1),
            Const(2),
            Const(3)
        )

        val result = applyTransform(emptyMap, tupleTransform)

        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun testObjectTransform() {
        val recordTransform = Record {
            "a" to 1
            "b" to 2
        }

        val result = applyTransform(emptyMap, recordTransform)

        assertEquals(mapOf("a" to 1, "b" to 2), result)
    }

    @Test
    fun `test list transform`() {
        val data: Data = listOf(1, 2, 3)
        val spec = ListOf { Const(42) }

        val result = applyTransform(data, spec)

        assertEquals(listOf(42, 42, 42), result)
    }

    @Test
    fun `list transform on unfitting data`() {
        val data: Data = mapOf(1 to "a", 2 to "b")
        val spec = ListOf { Const(3) }

        val result = applyTransform(data, spec) // returns empty list as data is not a list type

        assertEquals(listOf(3), result)
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
    fun `extend with existing key`() {
        val extensionTransform = Extension {
            "a" to 3
        }
        val result = applyTransform(mapOf("a" to 4,), extensionTransform)

        assertEquals(mapOf("a" to 3), result) // remains unchanged
    }

    @Test
    fun testRenameTransform() {
        val data = mapOf(
            "a" to 1,
            "b" to 2
        )

        val renameWithPairs = Rename.WithPairs(mapOf("a" to "x", "b" to "y"))

        val firstResult = applyTransform(data, renameWithPairs)

        assertEquals(mapOf("x" to 1, "y" to 2), firstResult)

        val renameWithFunc = Rename.WithFunc { it.uppercase() }

        val secondResult = applyTransform(data, renameWithFunc)

        assertEquals(mapOf("A" to 1, "B" to 2), secondResult)
    }

    @Test // Rename on wrong data type returns empty map ?!?
    fun `rename on list`() {
        val data = listOf(1, 2, 3)

        val renameWithPairs = Rename.WithPairs(mapOf("a" to "x", "b" to "y"))

        val firstResult = applyTransform(data, renameWithPairs)

        assertEquals(emptyMap, firstResult)

        val renameWithFunc = Rename.WithFunc { it.uppercase() }

        val secondResult = applyTransform(data, renameWithFunc)

        assertEquals(emptyMap, secondResult)
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