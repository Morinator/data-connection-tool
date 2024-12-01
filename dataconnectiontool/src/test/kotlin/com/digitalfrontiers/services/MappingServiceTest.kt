package com.digitalfrontiers.services

import com.digitalfrontiers.components.DummySink
import com.digitalfrontiers.components.DummySource
import com.digitalfrontiers.transform.Input
import com.digitalfrontiers.transform.Specification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MappingServiceTest {

    private val dummyMappingService = MappingService(
        sources = SourceService(listOf(DummySource())),
        transforms = TransformService(),
        sinks = SinkService(listOf(DummySink()))
    )

    @Test
    fun `throws error if not record`() {


        assertThrows<ClassCastException> { // class java.lang.String cannot be cast to class java.util.Map
            dummyMappingService.map(
                sourceId = "Dummy",
                sinkId = "Dummy",
                spec = Specification.Const("some constant string 123")
            )
        }
    }

    @Test
    fun `simple record can be used as spec`() {

        dummyMappingService.map(
            sourceId = "Dummy",
            sinkId = "Dummy",
            spec = Specification.Record {
                "a" to 1
                "b" to 2
            }
        )
    }

    @Test
    fun `test MappingService`() {

        //given
        val source = DummySource()
        val spec = Specification.Record {
            "x" to Input("a")
            "y" to Input("b")
        }
        val sink = DummySink()

        // when
        val mappingService = MappingService(
            sources = SourceService(listOf(source)),
            transforms = TransformService(), // TODO use automatically?
            sinks = SinkService(listOf(sink))
        )
        mappingService.map(
            sourceId = "Dummy",
            sinkId = "Dummy",
            spec = spec
        )

        // then
        assertEquals(mapOf("x" to "A_value", "y" to "B_value"), sink.storage[0])
    }
}