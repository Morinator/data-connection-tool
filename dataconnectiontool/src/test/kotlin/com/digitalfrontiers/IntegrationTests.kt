package com.digitalfrontiers

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
) {

    @Test
    fun `unused source ID`() {
        val specString = """{
            "type": "Record",
            "entries": {
                "key1": { "type": "Const", "value": 123 }
            }
        }"""

        val result: MvcResult = mockMvc.post("/mappings/invoke") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"source": "unused-source-id", "sink": "unused-sink-id", "spec": $specString}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value("false") }
            jsonPath("$.error") { value("Unknown source: unused-source-id") }
        }.andReturn()

        println(result.response.contentAsString)
    }

}