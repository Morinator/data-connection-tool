package com.digitalfrontiers.services

import com.digitalfrontiers.components.DummySink
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SinkServiceTest {

    private val sinkService = SinkService(sinks = listOf(DummySink()))

    @Test
    fun `throws exception on unknown id`() {
        assertThrows<IllegalArgumentException> {
            sinkService.put(
                "abc",
                listOf(
                    mapOf("a" to "b")
                )
            )
        }
    }
}