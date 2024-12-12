package com.digitalfrontiers

import com.digitalfrontiers.persistence.TransformationRepository
import com.digitalfrontiers.transform.Specification
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests @Autowired constructor(
    private val mockMvc: MockMvc,
    private val dummySink: DummySink,
    private val transformationRepository: TransformationRepository
) {

    private val stringRecordWithConst = """{
        "type": "record",
        "entries": {
            "key1": { "type": "const", "value": 123 }
        }
    }"""

    private val BASE_URL = "/api/v1"

    @Test
    fun `invoke --- record with constant entry`() {
        val result: MvcResult = mockMvc.post("$BASE_URL/invoke") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"source": "Dummy", "sink": "Dummy", "spec": $stringRecordWithConst}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value("true") }
        }.andReturn()

        println(result.response.contentAsString) // {"success":true}

        assertEquals(
            mutableListOf(
                mutableMapOf(
                    "key1" to 123,
                )
            ),
            dummySink.storage.last()
        )
    }


    @Nested
    inner class StoredMappingTesting {

        @Test
        fun `save and retrieve spec`() {

            val result: MvcResult = mockMvc.post("$BASE_URL/transformations/save") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"spec": $stringRecordWithConst}"""
            }.andExpect {
                status { isCreated() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.id") { isNumber() }
            }.andReturn()

            val id = JsonPath.parse(result.response.contentAsString).read<Int>("$.id")

            // Verify we can retrieve the saved specification
            val savedSpec = transformationRepository.getById(id.toLong())
            assertNotNull(savedSpec)
            assertTrue(savedSpec!!.data is Specification.Record)

            val record = savedSpec.data as Specification.Record
            assertEquals(123, (record.entries["key1"] as Specification.Const).value)
        }

        @Test
        fun `save specification --- invalid spec returns error`() {
            val invalidSpecString = """{
            "type": "InvalidType",
            "entries": {}
        }"""

            mockMvc.post("$BASE_URL/transformations/save") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"spec": $invalidSpecString}"""
            }.andExpect {
                status { isCreated() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error") { exists() }
            }
        }

        @Test
        fun `save specification --- multiple saves increment id`() {

            val ids = mutableListOf<Int>()
            repeat(5) {
                val result = mockMvc.post("$BASE_URL/transformations/save") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{"spec": $stringRecordWithConst}"""
                }.andReturn()

                val id = JsonPath.parse(result.response.contentAsString).read<Int>("$.id")
                ids.add(id)
            }

            // ID should be auto-incremented
            ids.zipWithNext { a, b -> assertEquals(a+1, b) }

            // Verify both specs are retrievable
            for (id in ids) {
                assertNotNull(transformationRepository.getById(id.toLong()))
            }
        }

        @Test
        fun `save mapping and invoke saved mapping`() {
            // First save the specification
            val saveResult = mockMvc.post("$BASE_URL/transformations/save") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"spec": $stringRecordWithConst}"""
            }.andExpect {
                status { isCreated() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.id") { isNumber() }
            }.andReturn()

            val id = JsonPath.parse(saveResult.response.contentAsString).read<Int>("$.id")

            // Then invoke the stored mapping
            mockMvc.post("$BASE_URL/transformations/invoke/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"source": "Dummy", "sink": "Dummy"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.success") { value(true) }
            }

            // Verify the result in the sink
            assertEquals(
                mutableListOf(
                    mutableMapOf(
                        "key1" to 123,
                    )
                ),
                dummySink.storage.last()
            )
        }

        @Test
        fun `invoke stored mapping --- non-existent id returns error`() {
            mockMvc.post("$BASE_URL/transformations/invoke/99999") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"source": "Dummy", "sink": "Dummy"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error") { value("No specification found with id: 99999") }
            }
        }

        @Test
        fun `invoke stored mapping --- invalid source returns error`() {
            // First save a valid specification
            val saveResult = mockMvc.post("$BASE_URL/transformations/save") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"spec": $stringRecordWithConst}"""
            }.andReturn()

            val id = JsonPath.parse(saveResult.response.contentAsString).read<Int>("$.id")

            // Try to invoke with invalid source
            mockMvc.post("$BASE_URL/transformations/invoke/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"source": "NonExistentSource", "sink": "Dummy"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error") { exists() }
            }
        }

        @Test
        fun `invoke stored mapping --- invalid sink returns error`() {
            // First save a valid specification
            val saveResult = mockMvc.post("$BASE_URL/transformations/save") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"spec": $stringRecordWithConst}"""
            }.andReturn()

            val id = JsonPath.parse(saveResult.response.contentAsString).read<Int>("$.id")

            // Try to invoke with invalid sink
            mockMvc.post("$BASE_URL/transformations/invoke/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"source": "Dummy", "sink": "NonExistentSink"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.success") { value(false) }
                jsonPath("$.error") { exists() }
            }
        }

        @Test
        fun `invoke stored mapping --- malformed request body returns error`() {
            mockMvc.post("$BASE_URL/transformations/invoke/1") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"invalid": "json"}"""
            }.andExpect {
                status { isBadRequest() }  // Expect HTTP 400 Bad Request
            }
        }

        @Test
        fun `save transformation, verify presence, delete it, verify absence`() {
            // First save the transformation
            val saveResult = mockMvc.post("$BASE_URL/transformations/save") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"spec": $stringRecordWithConst}"""
            }.andExpect {
                status { isCreated() }
                jsonPath("$.success") { value(true) }
                jsonPath("$.id") { isNumber() }
            }.andReturn()

            val id = JsonPath.parse(saveResult.response.contentAsString).read<Int>("$.id") // 1

            // Verify the transformation exists
            assertNotNull(transformationRepository.getById(id.toLong()))

            // Delete the transformation
            mockMvc.delete("$BASE_URL/transformations/$id") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }

            val x = transformationRepository.getById(id.toLong())

            // Verify the transformation no longer exists
            assertNull(transformationRepository.getById(id.toLong()))
        }

    }
}