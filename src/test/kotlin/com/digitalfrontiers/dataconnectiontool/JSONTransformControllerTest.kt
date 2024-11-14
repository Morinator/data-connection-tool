package com.digitalfrontiers.dataconnectiontool

import com.digitalfrontiers.dataconnectiontool.controller.JSONTransformController
import com.digitalfrontiers.dataconnectiontool.controller.TransformationRequestBody
import com.digitalfrontiers.dataconnectiontool.service.IStorageService
import com.digitalfrontiers.dataconnectiontool.service.ITransformationService
import com.digitalfrontiers.dataconnectiontool.util.JsonUtils
import com.digitalfrontiers.dataconnectiontool.util.parseTransformConfig
import com.digitalfrontiers.datatransformlang.transform.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.springframework.http.MediaType
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import io.mockk.*

@WebMvcTest(JSONTransformController::class, excludeAutoConfiguration = [SecurityAutoConfiguration::class])
class JSONTransformControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var storage: IStorageService<String>

    @MockBean
    private lateinit var transformer: ITransformationService<String, String>

    @Test
    fun `storeTransformSpec should store spec string`() {
        val specId = "testSpec"
        val specString = """{"key":"value"}"""

        mockMvc.perform(
            put("/transforms/$specId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(specString)
        )
            .andExpect(status().isOk)

        verify(storage).store("specs/$specId", specString)
    }

    @Test
    fun `applyTransform should return transformed string`() {

        // Setup and Mocking
        val specString = """
            {
                "type": "Self"
            }
        """.trimIndent()

        `when`(storage.load(anyString())).thenReturn(specString)

        mockkObject(JsonUtils)
        every { JsonUtils.toJsonString(any()) } returns """{}"""

        `when`(transformer.transform(anyString(), any(), anyString(), anyString())).thenReturn("""{}""")

        // Test Execution
        val specId = "testSpec"

        val payload = """
            {
                "data": {}
            }
        """.trimIndent()

        mockMvc.perform(
            post("/transforms/$specId/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )

        verify(transformer).transform("""{}""", Self, null, null)
    }
}
