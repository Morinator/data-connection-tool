package com.digitalfrontiers.controllers

import com.digitalfrontiers.services.MappingService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@Import(TestConfig::class) // used in MappingController
@WebMvcTest
class MappingControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test 1`() {

        mockMvc.perform(
            post("/mappings/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"source": "unused-source-id", "sink": "unused-sink-id", "spec": {}}""")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("false"))
    }
}

@Configuration
class TestConfig {

    @Bean
    fun mappingService(): MappingService = mockk<MappingService>().also {
        every {
            it.validate(any(), any(), any())
        } returns false
    }
}