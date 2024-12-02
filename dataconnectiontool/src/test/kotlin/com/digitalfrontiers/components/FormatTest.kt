package com.digitalfrontiers.components

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class FormatTest {

    @Test
    fun `has mandatory and optional fields`() {
        val format = Format(
            mandatoryFields = listOf("a", "b"),
            optionalFields = listOf("c", "d", "e"),
        )

        assertEquals(listOf("a", "b", "c", "d", "e"), format.getAllFields())
    }

    @Test
    fun `has only mandatory fields`() {
        val format = Format(
            mandatoryFields = listOf("a", "b"),
            optionalFields = listOf(),
        )

        assertEquals(listOf("a", "b"), format.getAllFields())
    }

    @Test
    fun `has only optional fields`() {
        val format = Format(
            mandatoryFields = listOf(),
            optionalFields = listOf("c", "d", "e"),
        )

        assertEquals(listOf("c", "d", "e"), format.getAllFields())
    }
}