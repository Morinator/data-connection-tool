package com.digitalfrontiers

import com.digitalfrontiers.persistence.MappingRepository
import com.digitalfrontiers.transform.Transformation
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IntegrationTests @Autowired constructor(
    private val mockMvc: MockMvc,
    private val dummySink: DummySink,
    private val mappingRepository: MappingRepository
) {

    private val stringRecordWithConst = """{
        "type": "record",
        "entries": {
            "key1": { "type": "const", "value": 123 }
        }
    }"""

    private val BASE_URL = "/api/v1"

    @Nested
    inner class StoredMappingTesting {

        @Test
        fun `save and retrieve transformation`() {

            val result: MvcResult = mockMvc.post("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $stringRecordWithConst}"""
            }.andExpect {
                status { isCreated() }
                header { exists("Location") }
            }.andReturn()

            val resourceUrl: String = result.response.getHeaderValue("Location").toString()
            val id = resourceUrl.split("/").last().toLong()

            // Verify we can retrieve the saved transformation
            val savedTransformation = mappingRepository.getById(id.toLong())
            assertNotNull(savedTransformation)
            assertTrue(savedTransformation!!.data is Transformation.Record)

            val record = savedTransformation.data as Transformation.Record
            assertEquals(123, (record.entries["key1"] as Transformation.Const).value)
        }

        @Test
        fun `save transformation --- invalid transformation returns error`() {
            val invalidTransformationString = """{
            "type": "InvalidType",
            "entries": {}
        }"""

            mockMvc.post("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $invalidTransformationString}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `save transformation --- multiple saves increment id`() {

            val ids = mutableListOf<Int>()
            repeat(5) {
                val result = mockMvc.post("$BASE_URL/mappings") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{"transformation": $stringRecordWithConst}"""
                }.andExpect {
                    status { isCreated() }
                    header {
                        exists("Location")
                    }
                }.andReturn()

                val resourceUrl: String = result.response.getHeaderValue("Location").toString()
                val id = resourceUrl.split("/").last().toInt()
                ids.add(id)
            }

            // ID should be auto-incremented
            ids.zipWithNext { a, b -> assertEquals(a+1, b) }

            // Verify both transformations are retrievable
            for (id in ids) {
                assertNotNull(mappingRepository.getById(id.toLong()))
            }
        }

        @Test
        fun `save mapping and invoke saved mapping`() {
            // First save the transformation
            val saveResult = mockMvc.post("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $stringRecordWithConst}"""
            }.andExpect {
                status { isCreated() }
                header {
                    exists("Location")
                    string("Location", "$BASE_URL/mappings/1")
                }
            }.andReturn()

            val resourceUrl: String = saveResult.response.getHeaderValue("Location").toString()

            // Then invoke the stored mapping
            mockMvc.post("$resourceUrl/invoke") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"source": "Dummy", "sink": "Dummy"}"""
            }.andExpect {
                status { isNoContent() }
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
            mockMvc.post("$BASE_URL/mappings/99999/invoke") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"source": "Dummy", "sink": "Dummy"}"""
            }.andExpect {
                status { isNotFound() }
            }
        }

        // TODO: Invalid Mappings should never be stored. Remove or replace with more meaningful test.
        @Disabled
        @Test
        fun `invoke stored mapping --- invalid source returns error`() {
            // First save a valid transformation
            val saveResult = mockMvc.post("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $stringRecordWithConst}"""
            }.andReturn()

            val id = JsonPath.parse(saveResult.response.contentAsString).read<Int>("$.id")

            // Try to invoke with invalid source
            mockMvc.post("$BASE_URL/mappings/$id/invoke") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"source": "NonExistentSource", "sink": "Dummy"}"""
            }.andExpect {
                status { isNotFound() }
                jsonPath("$.error") { exists() }
            }
        }

        // TODO: Invalid Mappings should never be stored. Remove or replace with more meaningful test.
        @Disabled
        @Test
        fun `invoke stored mapping --- invalid sink returns error`() {
            // First save a valid transformation
            val saveResult = mockMvc.post("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $stringRecordWithConst}"""
            }.andExpect {
                status { isCreated() }
                header { exists("Location") }
            }.andReturn()

            val resourceUrl: String = saveResult.response.getHeaderValue("Location").toString()

            // Try to invoke with invalid sink
            mockMvc.post("$resourceUrl/invoke") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"source": "Dummy", "sink": "NonExistentSink"}"""
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun `invoke stored mapping --- malformed request body returns error`() {
            mockMvc.post("$BASE_URL/mappings/1/invoke") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"invalid": "json"}"""
            }.andExpect {
                status { isBadRequest() }  // Expect HTTP 400 Bad Request
            }
        }

        @Test
        fun `save transformation, verify presence, delete it, verify absence`() {
            // First save the transformation
            val saveResult = mockMvc.post("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $stringRecordWithConst}"""
            }.andExpect {
                status { isCreated() }
                header { exists("Location") }
            }.andReturn()

            val resourceUrl: String = saveResult.response.getHeaderValue("Location").toString()
            val id = resourceUrl.split("/").last().toLong()

            // Verify the transformation exists
            assertNotNull(mappingRepository.getById(id))

            // Delete the transformation
            mockMvc.delete(resourceUrl) {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isNoContent() }
            }

            // Verify the transformation no longer exists
            assertNull(mappingRepository.getById(id))
        }

        @Test
        fun `update transformation --- successful update`() {
            // First save a transformation
            val saveResult = mockMvc.post("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $stringRecordWithConst}"""
            }.andExpect {
                status { isCreated() }
                header {
                    exists("Location")
                }
            }.andReturn()

            val resourceUrl: String = saveResult.response.getHeaderValue("Location").toString()
            val id = resourceUrl.split("/").last().toLong()

            // Update with new transformation
            val updatedTransformation = """{
                "type": "record",
                "entries": {
                    "key1": { "type": "const", "value": 456 }
                }
            }"""

            mockMvc.put(resourceUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $updatedTransformation}"""
            }.andExpect {
                status { isNoContent() }
            }

            // Verify the transformation was updated
            val savedTransformation = mappingRepository.getById(id.toLong())
            assertNotNull(savedTransformation)
            assertTrue(savedTransformation!!.data is Transformation.Record)

            val record = savedTransformation.data as Transformation.Record
            assertEquals(456, (record.entries["key1"] as Transformation.Const).value)
        }

        @Test
        fun `update transformation --- non-existent id returns error`() {
            val transformation = """{
                "type": "record",
                "entries": {
                    "key1": { "type": "const", "value": 456 }
                }
            }"""

            mockMvc.put("$BASE_URL/mappings/99999") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $transformation}"""
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        fun `update transformation --- invalid transformation returns error`() {
            // First save a valid transformation
            val saveResult = mockMvc.post("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $stringRecordWithConst}"""
            }.andExpect {
                status { isCreated() }
                header {
                    exists("Location")
                }
            }.andReturn()

            val resourceUrl: String = saveResult.response.getHeaderValue("Location").toString()

            // Try to update with invalid transformation
            val invalidTransformation = """{
                "type": "InvalidType",
                "entries": {}
            }"""

            mockMvc.put(resourceUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = """{"transformation": $invalidTransformation}"""
            }.andExpect {
                status { isBadRequest() }
            }

            // Verify original transformation is unchanged
            val id = resourceUrl.split("/").last().toLong()

            val savedTransformation = mappingRepository.getById(id)
            assertNotNull(savedTransformation)
            assertTrue(savedTransformation!!.data is Transformation.Record)

            val record = savedTransformation.data as Transformation.Record
            assertEquals(123, (record.entries["key1"] as Transformation.Const).value)
            assertEquals(true, true)
        }

        @Test
        fun `update transformation --- malformed request body returns error`() {
            mockMvc.put("$BASE_URL/mappings/1") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"invalid": "json"}"""
            }.andExpect {
                status { isBadRequest() }  // Expect HTTP 400 Bad Request
            }
        }

        @Test
        fun `getAllTransformations --- returns empty list when no transformations exist`() {

            mockMvc.get("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
                jsonPath("$.length()") { value(0) }
            }
        }

        @Test
        fun `getAllTransformations --- returns all saved transformations in correct order`() {

            val transformationValues = listOf(123, 456, 789)
            val savedIds = transformationValues.map { value ->
                val transformation = """{
                    "type": "record",
                    "entries": {
                        "key1": { "type": "const", "value": $value }
                    }
                }"""

                val result = mockMvc.post("$BASE_URL/mappings") {
                    contentType = MediaType.APPLICATION_JSON
                    content = """{"transformation": $transformation}"""
                }.andExpect {
                    status { isCreated() }
                    header { exists("Location") }
                }.andReturn()

                val resourceUrl: String = result.response.getHeaderValue("Location").toString()

                return@map resourceUrl.split("/").last().toLong()
            }

            // Get all transformations and verify
            val result = mockMvc.get("$BASE_URL/mappings") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
                jsonPath("$.length()") { value(3) }

                // Verify order (should be newest first due to ORDER BY created_at DESC)
                jsonPath("$[0].id") { value(savedIds[2]) }
                jsonPath("$[1].id") { value(savedIds[1]) }
                jsonPath("$[2].id") { value(savedIds[0]) }
            }.andReturn()

            // Verify the actual transformation data
            val transformations = JsonPath.parse(result.response.contentAsString)
                .read<List<Map<String, Any>>>("$")

            transformations.forEachIndexed { index, transformation ->
                val data = transformation["data"] as Map<*, *>
                val entries = (data["entries"] as Map<*, *>)["key1"] as Map<*, *>
                val expectedValue = transformationValues[transformationValues.size - 1 - index]
                assertEquals(expectedValue, entries["value"])
            }
        }

    }
}