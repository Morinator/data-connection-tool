package com.digitalfrontiers.services

import com.digitalfrontiers.components.DummySink
import com.digitalfrontiers.components.DummySource
import com.digitalfrontiers.transform.Specification
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MappingServiceTest {

    private val dummyMappingService = MappingService(
        sourceProvider = SourceService(listOf(DummySource())),
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
}