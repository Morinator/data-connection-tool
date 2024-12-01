package com.digitalfrontiers.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InterpolateTest {

    private val interpolate = Interpolate()

    @Test
    fun `test single placeholder`() {
        val result = interpolate.implementation(listOf("Hello, {}!", "World"))
        assertEquals("Hello, World!", result)
    }

    @Test
    fun `test multiple placeholders`() {
        val result = interpolate.implementation(listOf("My name is {} and I am {} years old.", "Alice", 25))
        assertEquals("My name is Alice and I am 25 years old.", result)
    }

    @Test
    fun `test excess arguments`() {
        val result = interpolate.implementation(listOf("This is {}", "fine", "extra"))
        assertEquals("This is fine", result)
    }

    @Test
    fun `test missing arguments`() {
        val result = interpolate.implementation(listOf("This is {}, and this is {}.", "fine"))
        assertEquals("This is fine, and this is {}.", result)
    }

    @Test
    fun `test no placeholders`() {
        val result = interpolate.implementation(listOf("No placeholders here.", "extra"))
        assertEquals("No placeholders here.", result)
    }

    @Test
    fun `test empty string`() {
        val result = interpolate.implementation(listOf("", "any"))
        assertEquals("", result)
    }

    @Test
    fun `test empty argument list`() {
        assertThrows<IndexOutOfBoundsException> {
            interpolate.implementation(emptyList())
        }
    }

    @Test
    fun `test first argument not a string`() {
        val exception = assertThrows<ClassCastException> {
            interpolate.implementation(listOf(123, "value"))
        }
        assertTrue(exception.message!!.contains("cannot be cast"))
    }

    @Test
    fun `test null arguments`() {
        val result = interpolate.implementation(listOf("Hello, {} and {}!", null, "World"))
        assertEquals("Hello, null and World!", result)
    }

    @Test
    fun `test placeholders in replacement arguments`() {
        val result = interpolate.implementation(listOf("Nested: {}", "{}"))
        assertEquals("Nested: {}", result)
    }

    @Test
    fun `test consecutive placeholders`() {
        val result = interpolate.implementation(listOf("{}{}{}", "A", "B", "C"))
        assertEquals("ABC", result)
    }

}