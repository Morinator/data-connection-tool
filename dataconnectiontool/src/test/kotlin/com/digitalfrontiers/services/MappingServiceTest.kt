package com.digitalfrontiers.services

import com.digitalfrontiers.components.DummySink
import com.digitalfrontiers.components.DummySource
import com.digitalfrontiers.transform.Record
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MappingServiceTest {

    private val dummyMappingService = MappingService(
        sources = SourceService(listOf(DummySource())),
        transforms = TransformService(),
        sinks = SinkService(listOf(DummySink()))
    )

    @Test
    fun `simple record can be used as spec`() {

        dummyMappingService.map(
            sourceId = "Dummy",
            sinkId = "Dummy",
            spec =  Record {
                "a" to 1
                "b" to 2
            }
        )
    }

    @Test
    fun `more complex example`() {

        //given
        val source = DummySource()
        val spec = Record {
            "x" from "a"
            "y" from "b"
        }
        val sink = DummySink()

        // when
        val mappingService = MappingService(
            sources = SourceService(listOf(source)),
            transforms = TransformService(),
            sinks = SinkService(listOf(sink))
        )

        mappingService.map(
            sourceId = "Dummy",
            sinkId = "Dummy",
            spec = spec
        )

        // then
        assertEquals(
            listOf(mapOf("x" to "A_value", "y" to "B_value")),
            sink.storage[0]
        )
    }
}