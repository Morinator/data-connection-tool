package com.digitalfrontiers.services

import com.digitalfrontiers.DummySink
import com.digitalfrontiers.DummySource
import com.digitalfrontiers.transform.Const
import com.digitalfrontiers.transform.Transformation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MappingServiceTest {

    private val dummyMappingService = MappingService(
        SourceService(listOf(DummySource())),
        TransformService(),
        SinkService(listOf(DummySink()))
    )

    private fun validateWithDummySourceAndSink(transformation: Transformation) : Boolean =
        dummyMappingService.validate(
            sourceId = "Dummy",
            sinkId = "Dummy",
            transformation = transformation
    )

    @Test
    fun `simple record can be used as transformation`() {

        dummyMappingService.map(
            sourceId = "Dummy",
            sinkId = "Dummy",
            record =  Transformation.Record {
                "a" to 1
                "b" to 2
            }
        )
    }

    @Test
    fun `one required field & one optional field`() {

        val source = DummySource()
        val transformation = Transformation.Record {
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
            record = transformation
        )

        assertEquals(
            listOf(mapOf("x" to "A_value", "y" to "B_value")),
            sink.storage[0]
        )
    }

    @Test
    fun `both sides only use required fields`() {

        val transformation = Transformation.Record {
            "x" from "a"
        }
        assertFalse(validateWithDummySourceAndSink(transformation)) // one required field is missing in sink
    }

    @Test
    fun `required and optional fields are set`() {

        val transformation = Transformation.Record {
            "x" from "a"
            "y" from "b"
            "z" from "c"
        }

        assertTrue(validateWithDummySourceAndSink(transformation))
    }

    @Test
    fun `required field is missing`() {

        val transformation = Transformation.Record {
            "y" from "a"
            "z" from "c"
        }

        assertFalse(validateWithDummySourceAndSink(transformation))
    }

    @Test
    fun `field is not used by sink`() {

        val transformation = Transformation.Record {
            "x" from "a"
            "someUnusedFieldName" from "a"
        }
        assertFalse(validateWithDummySourceAndSink(transformation))
    }

    @Test
    fun `source field is only optional instead of required`() {

        val transformation = Transformation.Record {
            "x" from "a"
            "y" from "c" // "c" is not required
        }

        assertFalse(validateWithDummySourceAndSink(transformation))
    }

    @Test
    fun `not all required source fields have to be used`() {

        val transformation = Transformation.Record {
            "x" from "a"
            "y" from "a"
            "z" from "a"
        }
        assertTrue(validateWithDummySourceAndSink(transformation))
    }

    @Test
    fun `field not provided by source`() {

        val transformation = Transformation.Record {
            "x" from "a"
            "y" from "b"
            "z" from "nonExistingFieldName"
        }

        assertFalse(validateWithDummySourceAndSink(transformation))
    }

    @Test
    fun `required field depends on optional field`() {

        val transformation = Transformation.Record {
            "x" from "c"
        }

        assertFalse(validateWithDummySourceAndSink(transformation))
    }

    @Test
    fun `required sink fields are set by Const`() {

        val transformation = Transformation.Record {
            "x" to Const("test123")
            "y" to Const("test123")
        }

        assertTrue(validateWithDummySourceAndSink(transformation))
    }

    @Test
    fun `optional sink fields are set by Const`() {

        val transformation = Transformation.Record {
            "x" from "a"
            "y" from "b"
            "z" to Const("test123")
        }

        assertTrue(validateWithDummySourceAndSink(transformation))
    }

    @Test
    fun `all sink fields are set by Const`() {

        val transformation = Transformation.Record {
            "x" to Const("test123")
            "y" to Const("test123")
            "z" to Const("test123")
        }

        assertTrue(validateWithDummySourceAndSink(transformation))
    }
}