package com.digitalfrontiers

import com.digitalfrontiers.components.DummySink
import com.digitalfrontiers.components.DummySource
import com.digitalfrontiers.transform.Input
import com.digitalfrontiers.transform.Specification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TransformTest {

    @Test
    fun `simple transformation`() {

        //given
        val jsonSink = DummySink()
        val x = DummySource().fetch()
        val spec = Specification.Record {
            "x" to Input("a")
            "y" to Input("b")
        }

        // when
        val transformed: Map<String, String> = (Transform to { spec }).apply(x) as Map<String, String>
        jsonSink.put(transformed)

        // then
        assertEquals(mapOf("x" to "A_value", "y" to "B_value"), jsonSink.storage[0])
    }
}