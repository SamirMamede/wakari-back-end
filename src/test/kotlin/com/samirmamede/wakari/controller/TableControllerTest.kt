package com.samirmamede.wakari.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.samirmamede.wakari.dto.TableRequest
import com.samirmamede.wakari.dto.TableResponse
import com.samirmamede.wakari.dto.TableStatusUpdateRequest
import com.samirmamede.wakari.dto.TableUpdateRequest
import com.samirmamede.wakari.model.RestaurantTable
import com.samirmamede.wakari.model.TableStatus
import com.samirmamede.wakari.service.TableService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TableControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    private lateinit var tableService: TableService

    @InjectMocks
    private lateinit var tableController: TableController

    private lateinit var table1: RestaurantTable
    private lateinit var table2: RestaurantTable

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(tableController)
            .setControllerAdvice(com.samirmamede.wakari.exception.GlobalExceptionHandler())
            .build()
        objectMapper = ObjectMapper()
        
        table1 = RestaurantTable(
            id = 1L,
            number = 1,
            capacity = 4,
            status = TableStatus.AVAILABLE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        table2 = RestaurantTable(
            id = 2L,
            number = 2,
            capacity = 6,
            status = TableStatus.OCCUPIED,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    @Test
    fun `deve retornar todas as mesas paginadas`() {
        // Arrange
        val tables = listOf(table1, table2)
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(tables, pageable, tables.size.toLong())
        
        `when`(tableService.findAll(any())).thenReturn(page)

        // Act & Assert
        mockMvc.perform(get("/api/v1/tables")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[1].id").value(2))
            
        verify(tableService, times(1)).findAll(any())
    }

    @Test
    fun `deve retornar mesa por ID`() {
        // Arrange
        `when`(tableService.findById(1L)).thenReturn(table1)

        // Act & Assert
        mockMvc.perform(get("/api/v1/tables/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.number").value(1))
            .andExpect(jsonPath("$.capacity").value(4))
            .andExpect(jsonPath("$.status").value("AVAILABLE"))
            
        verify(tableService, times(1)).findById(1L)
    }

    @Test
    fun `deve criar nova mesa`() {
        // Arrange
        val request = TableRequest(
            number = 3,
            capacity = 8
        )
        
        val createdTable = RestaurantTable(
            id = 3L,
            number = 3,
            capacity = 8,
            status = TableStatus.AVAILABLE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(tableService.create(any())).thenReturn(createdTable)

        // Act & Assert
        mockMvc.perform(post("/api/v1/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.number").value(3))
            .andExpect(jsonPath("$.capacity").value(8))
            .andExpect(jsonPath("$.status").value("AVAILABLE"))
            
        verify(tableService, times(1)).create(any())
    }

    @Test
    fun `deve atualizar mesa existente`() {
        // Arrange
        val request = TableUpdateRequest(
            number = 1,
            capacity = 10,
            status = TableStatus.RESERVED
        )
        
        val updatedTable = RestaurantTable(
            id = 1L,
            number = 1,
            capacity = 10,
            status = TableStatus.RESERVED,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(tableService.update(eq(1L), any())).thenReturn(updatedTable)

        // Act & Assert
        mockMvc.perform(put("/api/v1/tables/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.number").value(1))
            .andExpect(jsonPath("$.capacity").value(10))
            .andExpect(jsonPath("$.status").value("RESERVED"))
            
        verify(tableService, times(1)).update(eq(1L), any())
    }

    @Test
    fun `deve atualizar status da mesa`() {
        // Arrange
        val request = TableStatusUpdateRequest(
            status = TableStatus.CLEANING
        )
        
        val updatedTable = RestaurantTable(
            id = 1L,
            number = 1,
            capacity = 4,
            status = TableStatus.CLEANING,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        `when`(tableService.updateStatus(1L, TableStatus.CLEANING)).thenReturn(updatedTable)

        // Act & Assert
        mockMvc.perform(patch("/api/v1/tables/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("CLEANING"))
            
        verify(tableService, times(1)).updateStatus(1L, TableStatus.CLEANING)
    }

    @Test
    fun `deve excluir mesa`() {
        // Arrange
        doNothing().`when`(tableService).delete(1L)

        // Act & Assert
        mockMvc.perform(delete("/api/v1/tables/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent)
            
        verify(tableService, times(1)).delete(1L)
    }

    @Test
    fun `deve limpar mesas em limpeza`() {
        // Arrange
        doNothing().`when`(tableService).cleanupTables()

        // Act & Assert
        mockMvc.perform(post("/api/v1/tables/cleanup")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            
        verify(tableService, times(1)).cleanupTables()
    }
} 