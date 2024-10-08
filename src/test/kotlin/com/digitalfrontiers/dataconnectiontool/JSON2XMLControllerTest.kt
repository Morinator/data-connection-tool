package com.digitalfrontiers.dataconnectiontool

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.File

@WebMvcTest(JSON2XMLController::class)
class JSON2XMLControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `test convertJsonToXml endpoint`() {
        // Given
        val inputJsonText = File("dummy_data/json/john_doe.json").readText()
        val inputJsonNode = objectMapper.readTree(inputJsonText)

        val expectedXml = File("dummy_data/xml/john_doe.xml").readText()

        // When/Then
        mockMvc.perform(post("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .content(inputJsonText))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_XML))
            .andExpect(content().xml(expectedXml))
    }
}