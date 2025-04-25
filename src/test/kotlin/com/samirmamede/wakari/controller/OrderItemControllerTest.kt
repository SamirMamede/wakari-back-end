package com.samirmamede.wakari.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.samirmamede.wakari.dto.OrderItemResponse
import com.samirmamede.wakari.dto.OrderItemUpdateRequest
import com.samirmamede.wakari.exception.GlobalExceptionHandler
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.model.*
import com.samirmamede.wakari.repository.OrderItemRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.eq
import org.mockito.Mockito.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class OrderItemControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var orderItemRepository: OrderItemRepository
    private lateinit var objectMapper: ObjectMapper
    private lateinit var testUser: User
    private lateinit var testMenuItem: MenuItem
    private lateinit var testOrder: Order
    private lateinit var testOrderItem: OrderItem

    @BeforeEach
    fun setup() {
        orderItemRepository = org.mockito.Mockito.mock(OrderItemRepository::class.java)
        val orderItemController = OrderItemController(orderItemRepository)
        
        mockMvc = MockMvcBuilders.standaloneSetup(orderItemController)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
            
        objectMapper = ObjectMapper().findAndRegisterModules()
        
        // Configurar dados de teste
        testUser = User(
            id = 1L,
            name = "Usuário Teste",
            email = "usuario@teste.com",
            password = "senha123",
            role = UserRole.CUSTOMER
        )
        
        testMenuItem = MenuItem(
            id = 1L,
            name = "Produto Teste",
            description = "Descrição do produto",
            price = BigDecimal("15.90"),
            available = true,
            category = "Categoria Teste"
        )
        
        testOrder = Order(
            id = 1L,
            user = testUser,
            total = BigDecimal("31.80"),
            status = OrderStatus.PENDING,
            isDelivery = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        testOrderItem = OrderItem(
            id = 1L,
            order = testOrder,
            menuItem = testMenuItem,
            quantity = 2,
            priceAtTime = BigDecimal("15.90"),
            notes = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    @Test
    fun `findById should return order item when it exists`() {
        // Arrange
        val orderItemId = 1L
        
        `when`(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(testOrderItem))
        
        // Act & Assert
        mockMvc.perform(get("/api/order-items/$orderItemId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(orderItemId))
            .andExpect(jsonPath("$.menuItemId").value(testMenuItem.id))
            .andExpect(jsonPath("$.quantity").value(2))
    }

    @Test
    fun `findById should return 404 when order item does not exist`() {
        // Arrange
        val orderItemId = 999L
        
        `when`(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty())
        
        // Act & Assert
        mockMvc.perform(get("/api/order-items/$orderItemId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `findByOrderId should return order items for specific order`() {
        // Arrange
        val orderId = 1L
        
        `when`(orderItemRepository.findByOrderId(orderId)).thenReturn(listOf(testOrderItem))
        
        // Act & Assert
        mockMvc.perform(get("/api/order-items/order/$orderId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(testOrderItem.id))
    }

    @Test
    fun `update should update order item quantity and notes`() {
        // Arrange
        val orderItemId = 1L
        val request = OrderItemUpdateRequest(
            quantity = 3,
            notes = "Observação de teste"
        )
        
        val updatedOrderItem = testOrderItem.copy()
        updatedOrderItem.quantity = 3
        updatedOrderItem.notes = "Observação de teste"
        
        `when`(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(testOrderItem))
        `when`(orderItemRepository.save(any(OrderItem::class.java))).thenReturn(updatedOrderItem)
        
        // Act & Assert
        mockMvc.perform(put("/api/order-items/$orderItemId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(orderItemId))
            .andExpect(jsonPath("$.quantity").value(3))
            .andExpect(jsonPath("$.notes").value("Observação de teste"))
    }

    @Test
    fun `update should return 404 when order item does not exist`() {
        // Arrange
        val orderItemId = 999L
        val request = OrderItemUpdateRequest(
            quantity = 3,
            notes = "Observação de teste"
        )
        
        `when`(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty())
        
        // Act & Assert
        mockMvc.perform(put("/api/order-items/$orderItemId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `delete should remove an order item`() {
        // Arrange
        val orderItemId = 1L
        
        `when`(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(testOrderItem))
        doNothing().`when`(orderItemRepository).deleteById(orderItemId)
        
        // Act & Assert
        mockMvc.perform(delete("/api/order-items/$orderItemId"))
            .andExpect(status().isNoContent)
            
        verify(orderItemRepository).deleteById(orderItemId)
    }

    @Test
    fun `delete should return 404 when order item does not exist`() {
        // Arrange
        val orderItemId = 999L
        
        `when`(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty())
        
        // Act & Assert
        mockMvc.perform(delete("/api/order-items/$orderItemId"))
            .andExpect(status().isNotFound)
    }
} 