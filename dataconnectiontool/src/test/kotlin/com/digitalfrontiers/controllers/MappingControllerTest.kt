package com.digitalfrontiers.controllers

import com.digitalfrontiers.services.MappingService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import jakarta.servlet.ServletException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class MappingControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var mappingService: MappingService

    private val BASE_URL = "/api/v1"

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `return expected validation result`(expectedValidationResult: Boolean) {

        every {
            mappingService.validate(any(), any(), any())
        } returns expectedValidationResult

        val specString = """{ "type": "Const", "value": 42 }"""
        mockMvc.perform(
            post("$BASE_URL/mappings/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"source": "unused-source-id", "sink": "unused-sink-id", "spec": $specString}""")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("""{"isValid":$expectedValidationResult}"""))


    }

    @Test
    fun `exception on malformed specification string`() {

        val specString = """{ "type": "ThisTypeIsMadeUp", "value": 1234 }"""
        assertThrows<ServletException> {
            mockMvc.perform(
                post("$BASE_URL/mappings/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"source": "unused-source-id", "sink": "unused-sink-id", "spec": $specString}""")
            )
        }
    }
}
