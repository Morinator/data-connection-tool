package com.digitalfrontiers.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InterpolateTest {

    private val f = Interpolate()

    @Test
    fun `test single placeholder`() {
        val result = f.implementation(listOf("Hello, {}!", "World"))
        assertEquals("Hello, World!", result)
    }

    @Test
    fun `test multiple placeholders`() {
        val result = f.implementation(listOf("My name is {} and I am {} years old.", "Alice", 25))
        assertEquals("My name is Alice and I am 25 years old.", result)
    }

    @Test
    fun `test excess arguments`() {
        val result = f.implementation(listOf("This is {}", "fine", "extra"))
        assertEquals("This is fine", result)
    }

    @Test
    fun `test missing arguments`() {
        val result = f.implementation(listOf("This is {}, and this is {}.", "fine"))
        assertEquals("This is fine, and this is {}.", result)
    }

    @Test
    fun `test no placeholders`() {
        val result = f.implementation(listOf("No placeholders here.", "extra"))
        assertEquals("No placeholders here.", result)
    }

    @Test
    fun `test empty string`() {
        val result = f.implementation(listOf("", "any"))
        assertEquals("", result)
    }

    @Test
    fun `test empty argument list`() {
        assertThrows<IndexOutOfBoundsException> {
            f.implementation(emptyList())
        }
    }

    @Test
    fun `test first argument not a string`() {
        val exception = assertThrows<ClassCastException> {
            f.implementation(listOf(123, "value"))
        }
        assertTrue(exception.message!!.contains("cannot be cast"))
    }

    @Test
    fun `test null arguments`() {
        val result = f.implementation(listOf("Hello, {} and {}!", null, "World"))
        assertEquals("Hello, null and World!", result)
    }

    @Test
    fun `test placeholders in replacement arguments`() {
        val result = f.implementation(listOf("Nested: {}", "{}"))
        assertEquals("Nested: {}", result)
    }

    @Test
    fun `test consecutive placeholders`() {
        val result = f.implementation(listOf("{}{}{}", "A", "B", "C"))
        assertEquals("ABC", result)
    }

}