package com.digitalfrontiers.components

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class FormatTest {

    @Test
    fun `has mandatory and optional fields`() {
        val format = Format(
            requiredFields = listOf("a", "b"),
            optionalFields = listOf("c", "d", "e"),
        )

        assertEquals(listOf("a", "b", "c", "d", "e"), format.getAllFields())
    }

    @Test
    fun `has only mandatory fields`() {
        val format = Format(
            requiredFields = listOf("a", "b"),
            optionalFields = listOf(),
        )

        assertEquals(listOf("a", "b"), format.getAllFields())
    }

    @Test
    fun `has only optional fields`() {
        val format = Format(
            requiredFields = listOf(),
            optionalFields = listOf("c", "d", "e"),
        )

        assertEquals(listOf("c", "d", "e"), format.getAllFields())
    }

    @Test
    fun `constructor should throw when fields overlap`() {
        val exception = assertThrows<IllegalArgumentException> {
            Format(
                requiredFields = listOf("id", "email"),
                optionalFields = listOf("phone", "email")
            )
        }

        assertEquals(
            "Fields cannot be both required and optional. Overlapping fields: [email]",
            exception.message
        )
    }
}