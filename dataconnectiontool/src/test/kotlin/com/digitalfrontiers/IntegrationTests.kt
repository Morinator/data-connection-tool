package com.digitalfrontiers

import com.digitalfrontiers.persistence.SpecificationRepository
import com.digitalfrontiers.transform.Specification
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests @Autowired constructor(
    private val mockMvc: MockMvc,
    private val dummySink: DummySink,
    private val specificationRepository: SpecificationRepository
) {

    private val stringRecordWithConst = """{
        "type": "Record",
        "entries": {
            "key1": { "type": "Const", "value": 123 }
        }
    }"""

    @Test
    fun `invoke --- record with constant entry`() {
        val result: MvcResult = mockMvc.post("/mappings/invoke") {
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

    @Test
    fun `save and retrieve spec`() {

        val result: MvcResult = mockMvc.post("/mappings/stored/save") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"spec": $stringRecordWithConst}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.id") { isNumber() }
        }.andReturn()

        val id = JsonPath.parse(result.response.contentAsString).read<Int>("$.id")

        // Verify we can retrieve the saved specification
        val savedSpec = specificationRepository.getById(id.toLong())
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

        mockMvc.post("/mappings/stored/save") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"spec": $invalidSpecString}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(false) }
            jsonPath("$.error") { exists() }
        }
    }

    @Test
    fun `save specification --- multiple saves increment id`() {

        val ids = mutableListOf<Int>()
        repeat(5) {
            val result = mockMvc.post("/mappings/stored/save") {
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
            assertNotNull(specificationRepository.getById(id.toLong()))
        }
    }
}