package com.digitalfrontiers.dataconnectiontool

import com.digitalfrontiers.dataconnectiontool.datamapping.MappingService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(JSON2XMLController::class)
class JSON2XMLControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var mappingService: MappingService

    @Test
    fun `test convertJsonToXml endpoint`() {
        // Given
        val inputJson = """{"name": "John Doe", "age": 30}"""
        val inputJsonNode = objectMapper.readTree(inputJson)

        val expectedXml = """
            <ObjectNode>
                <name>John Doe</name>
                <age>30</age>
            </ObjectNode>
        """.trimIndent()

        Mockito.`when`(mappingService.applyMapping(
            inputJsonNode,
            emptyList()
        )
        ).thenReturn(inputJsonNode)

        // When/Then
        mockMvc.perform(post("/convert")
            .contentType(MediaType.APPLICATION_JSON)
            .content(inputJson))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_XML))
            .andExpect(content().xml(expectedXml))
    }
}