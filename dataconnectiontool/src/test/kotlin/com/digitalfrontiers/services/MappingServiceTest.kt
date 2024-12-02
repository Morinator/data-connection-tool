package com.digitalfrontiers.services

import com.digitalfrontiers.components.DummySink
import com.digitalfrontiers.components.DummySource
import com.digitalfrontiers.transform.Record
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MappingServiceTest {

    private val dummyMappingService = MappingService(
        SourceService(listOf(DummySource())),
        TransformService(),
        SinkService(listOf(DummySink()))
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
    fun `mapping example 1`() {

        //given
        val source = DummySource()
        val spec = Record {
            "x" from "a"
            "y" from "b"
        }
        val sink = DummySink()

        // when
        val mappingService = MappingService(
            SourceService(listOf(source)),
            TransformService(),
            SinkService(listOf(sink))
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

    @Test
    fun `validation -- only mandatory fields are set`() {

        //given
        val source = DummySource()
        val spec = Record {
            "x" from "a"
            "y" from "b"
        }
        val sink = DummySink()

        // when
        val mappingService = MappingService(
            SourceService(listOf(source)),
            TransformService(),
            SinkService(listOf(sink))
        )

        // then
        assertTrue(mappingService.validateSink(
            sinkId = "Dummy",
            record = spec
        ))
    }

    @Test
    fun `validation -- mandatory and optional fields are set`() {

        //given
        val source = DummySource()
        val spec = Record {
            "x" from "a"
            "y" from "b"
            "z" from "c"
        }
        val sink = DummySink()

        // when
        val mappingService = MappingService(
            SourceService(listOf(source)),
            TransformService(),
            SinkService(listOf(sink))
        )

        // then
        assertTrue(mappingService.validateSink(
            sinkId = "Dummy",
            record = spec
        ))
    }

    @Test
    fun `validation -- mandatory field is missing`() {

        //given
        val source = DummySource()
        val spec = Record {
            "y" from "b"
            "z" from "c"
        }
        val sink = DummySink()

        // when
        val mappingService = MappingService(
            SourceService(listOf(source)),
            TransformService(),
            SinkService(listOf(sink))
        )

        // then
        assertFalse(mappingService.validateSink(
            sinkId = "Dummy",
            record = spec
        ))
    }

    @Test
    fun `validation -- field is not used by sink`() {

        //given
        val source = DummySource()
        val spec = Record {
            "x" from "a"
            "y" from "b"
            "z" from "c"
            "someUnusedFieldName" from "a"
        }
        val sink = DummySink()

        // when
        val mappingService = MappingService(
            SourceService(listOf(source)),
            TransformService(),
            SinkService(listOf(sink))
        )

        // then
        assertFalse(mappingService.validateSink(
            sinkId = "Dummy",
            record = spec
        ))
    }
}