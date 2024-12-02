package com.digitalfrontiers.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BranchOnEqualsTest {
    private val f = BranchOnEquals()

    @Test
    fun `test equal values`() {
        val result = f.implementation(listOf("a", "a", "b", "c"))
        assertEquals("b", result)
    }

    @Test
    fun `test unequal values`() {
        val result = f.implementation(listOf("a", "b", "c", "d"))
        assertEquals("d", result)
    }

    @Test
    fun `test null values - both null`() {
        val result = f.implementation(listOf(null, null, "a", "b"))
        assertEquals("a", result)
    }

    @Test
    fun `test null values - one null`() {
        val result = f.implementation(listOf(null, "b", "c", "d"))
        assertEquals("d", result)
    }

    @Test
    fun `test non-string values - equal`() {
        val result = f.implementation(listOf(42, 42, "a", "b"))
        assertEquals("a", result)
    }

    @Test
    fun `test non-string values - unequal`() {
        val result = f.implementation(listOf(42, 12345, "a", "b"))
        assertEquals("b", result)
    }

    @Test
    fun `test mixed types`() {
        val result = f.implementation(listOf("42", 42, "a", "b"))
        assertEquals("b", result)
    }

    @Test
    fun `test missing arguments`() {
        assertThrows<IndexOutOfBoundsException> {
            f.implementation(listOf("test", "test"))
        }
    }

    @Test
    fun `test empty arguments`() {
        assertThrows<IndexOutOfBoundsException> {
            f.implementation(emptyList())
        }
    }

}