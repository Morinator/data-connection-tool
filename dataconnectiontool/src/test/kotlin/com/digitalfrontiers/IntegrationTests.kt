package com.digitalfrontiers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
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
@Tag("integration")
class IntegrationTests @Autowired constructor(
    private val mockMvc: MockMvc,
    private val dummySink: DummySink
) {

    @Test
    fun `invoke --- record with constant entry`() {
        val specString = """{
            "type": "Record",
            "entries": {
                "key1": { "type": "Const", "value": 123 }
            }
        }"""

        val result: MvcResult = mockMvc.post("/mappings/invoke") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"source": "Dummy", "sink": "Dummy", "spec": $specString}"""
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

}