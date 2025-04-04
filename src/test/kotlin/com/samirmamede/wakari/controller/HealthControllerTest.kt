package com.samirmamede.wakari.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class HealthControllerTest {

    private lateinit var mockMvc: MockMvc

    @InjectMocks
    private lateinit var healthController: HealthController

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(healthController).build()
    }

    @Test
    fun `health endpoint should return UP status`() {
        // Act & Assert
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.app").value("Wakari API"))
            .andExpect(jsonPath("$.version").value("0.0.1"))
            .andExpect(jsonPath("$.timestamp").exists())
    }
} 