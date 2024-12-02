package com.digitalfrontiers

import com.digitalfrontiers.components.DummySink
import com.digitalfrontiers.components.DummySource
import com.digitalfrontiers.transform.Input
import com.digitalfrontiers.transform.ListOf
import com.digitalfrontiers.transform.Specification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TransformTest {

    @Test
    fun `simple transformation`() {

        //given
        val sink = DummySink()
        val x = DummySource().fetch()
        val spec = ListOf {
            Specification.Record {
                "x" from "a"
                "y" from "b"
            }
        }

        // when
        val transformed = (Transform to { spec }).apply(x) as List<Map<String, String>>
        sink.put(transformed)

        // then
        assertEquals(listOf(mapOf("x" to "A_value", "y" to "B_value")), sink.storage[0])
    }
}