package com.digitalfrontiers.controllers

import com.digitalfrontiers.services.MappingService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(MappingController::class)
class MappingControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var mappingService: MappingService



    @Test
    fun `validate mapping returns false on invalid spec`() {
        val requestBody = MappingRequestBody(
            source = "unused-source-id",
            sink = "unused-sink-id",
            spec = objectMapper.createObjectNode() // is empty, but has to be Record
        )

        mockMvc.perform(
            post("/mappings/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
        )
            .andExpect(status().isOk)
            .andExpect(content().string("false"))
    }

}