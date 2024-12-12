package com.digitalfrontiers.controllers

import com.digitalfrontiers.services.MappingService
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
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

        val transformationString = """{ "type": "const", "value": 42 }"""
        mockMvc.perform(
            post("$BASE_URL/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"source": "unused-source-id", "sink": "unused-sink-id", "transformation": $transformationString}""")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("""{"isValid":$expectedValidationResult}"""))


    }

    @Test
    fun `exception on malformed transformation string`() {

        val transformationString = """{ "type": "thisTypeIsMadeUp", "value": 1234 }"""
        assertThrows<InvalidTypeIdException> {
            mockMvc.perform(
                post("$BASE_URL/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"source": "unused-source-id", "sink": "unused-sink-id", "transformation": $transformationString}""")
            )
        }
    }
}
