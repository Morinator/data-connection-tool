package com.digitalfrontiers.services

import com.digitalfrontiers.components.DummySource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SourceServiceTest {

    private val sourceService = SourceService(sources = listOf(DummySource()))

    @Test
    fun `throws exception on unknown id`() {
        assertThrows<IllegalArgumentException> {
            sourceService.fetch(
                "abc"
            )
        }
    }
}