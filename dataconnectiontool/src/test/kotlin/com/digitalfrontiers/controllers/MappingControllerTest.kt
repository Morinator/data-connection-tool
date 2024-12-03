package com.digitalfrontiers.controllers

import com.digitalfrontiers.services.MappingService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest
class MappingControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var mappingService: MappingService

    @Test
    fun `test 1`() {

        every {
            mappingService.validate(any(), any(), any())
        } returns false

        mockMvc.perform(
            post("/mappings/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"source": "unused-source-id", "sink": "unused-sink-id", "spec": {}}""")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("false"))
    }
}
