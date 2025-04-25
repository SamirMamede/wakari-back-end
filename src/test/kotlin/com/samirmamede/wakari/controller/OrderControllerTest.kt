package com.samirmamede.wakari.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.samirmamede.wakari.dto.*
import com.samirmamede.wakari.exception.GlobalExceptionHandler
import com.samirmamede.wakari.exception.InvalidOperationException
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.model.Order
import com.samirmamede.wakari.model.OrderStatus
import com.samirmamede.wakari.model.User
import com.samirmamede.wakari.model.UserRole
import com.samirmamede.wakari.service.OrderService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class OrderControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var orderService: OrderService
    private lateinit var objectMapper: ObjectMapper
    private lateinit var testOrder: Order
    private lateinit var testOrderResponse: OrderResponse

    @BeforeEach
    fun setup() {
        orderService = org.mockito.Mockito.mock(OrderService::class.java)
        val orderController = OrderController(orderService)
        
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()

        objectMapper = ObjectMapper().findAndRegisterModules()
        
        // Configurar dados de teste
        val testUser = User(
            id = 1L,
            name = "Usuário Teste",
            email = "usuario@teste.com",
            password = "senha123",
            role = UserRole.CUSTOMER
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
        
        testOrderResponse = OrderResponse(
            id = 1L,
            userId = 1L,
            userName = "Usuário Teste",
            tableId = null,
            tableNumber = null,
            total = BigDecimal("31.80"),
            status = OrderStatus.PENDING,
            isDelivery = true,
            items = listOf(
                OrderItemResponse(
                    id = 1L,
                    menuItemId = 1L,
                    menuItemName = "Produto Teste",
                    quantity = 2,
                    priceAtTime = BigDecimal("15.90"),
                    subtotal = BigDecimal("31.80"),
                    notes = null,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            ),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    @Test
    fun `getAllOrders should return orders list`() {
        // Arrange
        val pageable = PageRequest.of(0, 10)
        val ordersPage = PageImpl(listOf(testOrder))
        
        `when`(orderService.findAll(any(Pageable::class.java))).thenReturn(ordersPage)
        
        // Act & Assert
        mockMvc.perform(get("/api/orders")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(1))
    }

    @Test
    fun `getOrderById should return order when it exists`() {
        // Arrange
        val orderId = 1L
        
        `when`(orderService.findById(orderId)).thenReturn(testOrder)
        
        // Act & Assert
        mockMvc.perform(get("/api/orders/$orderId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(orderId))
            .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.toString()))
    }

    @Test
    fun `getOrderById should return 404 when order does not exist`() {
        // Arrange
        val orderId = 999L
        
        `when`(orderService.findById(orderId))
            .thenThrow(ResourceNotFoundException("Pedido não encontrado com ID: $orderId"))
        
        // Act & Assert
        mockMvc.perform(get("/api/orders/$orderId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getOrdersByStatus should return orders with specific status`() {
        // Arrange
        val status = OrderStatus.PENDING
        val pageable = PageRequest.of(0, 10)
        val ordersPage = PageImpl(listOf(testOrder))
        
        `when`(orderService.findByStatus(eq(status), any(Pageable::class.java))).thenReturn(ordersPage)
        
        // Act & Assert
        mockMvc.perform(get("/api/orders/status/$status")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].status").value(status.toString()))
    }

    @Test
    fun `getOrdersByUser should return orders for specific user`() {
        // Arrange
        val userId = 1L
        val pageable = PageRequest.of(0, 10)
        val ordersPage = PageImpl(listOf(testOrder))
        
        `when`(orderService.findByUser(eq(userId), any(Pageable::class.java))).thenReturn(ordersPage)
        
        // Act & Assert
        mockMvc.perform(get("/api/orders/user/$userId")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].userId").value(userId))
    }

    @Test
    fun `getOrdersByDateRange should return orders in date range`() {
        // Arrange
        val startDate = LocalDate.now().minusDays(7)
        val endDate = LocalDate.now()
        val pageable = PageRequest.of(0, 10)
        val ordersPage = PageImpl(listOf(testOrder))
        
        `when`(orderService.findByDateRange(eq(startDate), eq(endDate), any(Pageable::class.java)))
            .thenReturn(ordersPage)
        
        val formatter = DateTimeFormatter.ISO_DATE
        
        // Act & Assert
        mockMvc.perform(get("/api/orders/date-range")
            .param("startDate", startDate.format(formatter))
            .param("endDate", endDate.format(formatter))
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(1))
    }

    @Test
    fun `createOrder should create a new order`() {
        // Arrange
        val request = OrderRequest(
            userId = 1L,
            tableId = null,
            isDelivery = true,
            items = listOf(
                OrderItemRequest(
                    menuItemId = 1L,
                    quantity = 2
                )
            )
        )
        
        `when`(orderService.create(
            eq(request.userId),
            eq(request.tableId),
            eq(request.isDelivery),
            eq(request.items)
        )).thenReturn(testOrder)
        
        // Act & Assert
        mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(testOrder.id))
            .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.toString()))
    }

    @Test
    fun `addItemsToOrder should add items to existing order`() {
        // Arrange
        val orderId = 1L
        val request = AddOrderItemsRequest(
            items = listOf(
                OrderItemRequest(
                    menuItemId = 2L,
                    quantity = 1
                )
            )
        )
        
        `when`(orderService.addItemsToOrder(eq(orderId), eq(request.items)))
            .thenReturn(testOrder)
        
        // Act & Assert
        mockMvc.perform(post("/api/orders/$orderId/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(orderId))
    }

    @Test
    fun `updateOrderStatus should update order status`() {
        // Arrange
        val orderId = 1L
        val request = OrderStatusUpdateRequest(
            status = OrderStatus.PREPARING
        )
        
        val updatedOrder = testOrder.copy(status = OrderStatus.PREPARING)
        
        `when`(orderService.updateStatus(eq(orderId), eq(request.status)))
            .thenReturn(updatedOrder)
        
        // Act & Assert
        mockMvc.perform(patch("/api/orders/$orderId/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(orderId))
            .andExpect(jsonPath("$.status").value(OrderStatus.PREPARING.toString()))
    }

    @Test
    fun `updateOrderStatus should return 400 for invalid status transition`() {
        // Arrange
        val orderId = 1L
        val request = OrderStatusUpdateRequest(
            status = OrderStatus.DELIVERED
        )
        
        `when`(orderService.updateStatus(eq(orderId), eq(request.status)))
            .thenThrow(InvalidOperationException("Um pedido pendente só pode ser alterado para 'em preparo' ou 'cancelado'"))
        
        // Act & Assert
        mockMvc.perform(patch("/api/orders/$orderId/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `cancelOrder should cancel an order`() {
        // Arrange
        val orderId = 1L
        val canceledOrder = testOrder.copy(status = OrderStatus.CANCELED)
        
        `when`(orderService.cancelOrder(orderId)).thenReturn(canceledOrder)
        
        // Act & Assert
        mockMvc.perform(post("/api/orders/$orderId/cancel"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(orderId))
            .andExpect(jsonPath("$.status").value(OrderStatus.CANCELED.toString()))
    }
} 