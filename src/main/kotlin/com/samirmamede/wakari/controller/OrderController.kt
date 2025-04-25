package com.samirmamede.wakari.controller

import com.samirmamede.wakari.dto.*
import com.samirmamede.wakari.model.OrderStatus
import com.samirmamede.wakari.service.OrderService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    /**
     * Lista todos os pedidos com paginação
     */
    @GetMapping
    fun getAllOrders(pageable: Pageable): ResponseEntity<Page<OrderResponse>> {
        val orders = orderService.findAll(pageable)
        val response = orders.map { OrderResponse.fromEntity(it) }
        return ResponseEntity.ok(response)
    }

    /**
     * Busca um pedido pelo ID
     */
    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        val order = orderService.findById(id)
        return ResponseEntity.ok(OrderResponse.fromEntity(order))
    }

    /**
     * Busca pedidos por status
     */
    @GetMapping("/status/{status}")
    fun getOrdersByStatus(
        @PathVariable status: OrderStatus,
        pageable: Pageable
    ): ResponseEntity<Page<OrderResponse>> {
        val orders = orderService.findByStatus(status, pageable)
        val response = orders.map { OrderResponse.fromEntity(it) }
        return ResponseEntity.ok(response)
    }

    /**
     * Busca pedidos de um usuário específico
     */
    @GetMapping("/user/{userId}")
    fun getOrdersByUser(
        @PathVariable userId: Long,
        pageable: Pageable
    ): ResponseEntity<Page<OrderResponse>> {
        val orders = orderService.findByUser(userId, pageable)
        val response = orders.map { OrderResponse.fromEntity(it) }
        return ResponseEntity.ok(response)
    }

    /**
     * Busca pedidos por período
     */
    @GetMapping("/date-range")
    fun getOrdersByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        pageable: Pageable
    ): ResponseEntity<Page<OrderResponse>> {
        val orders = orderService.findByDateRange(startDate, endDate, pageable)
        val response = orders.map { OrderResponse.fromEntity(it) }
        return ResponseEntity.ok(response)
    }

    /**
     * Cria um novo pedido
     */
    @PostMapping
    fun createOrder(@Valid @RequestBody request: OrderRequest): ResponseEntity<OrderResponse> {
        val createdOrder = orderService.create(
            userId = request.userId,
            tableId = request.tableId,
            isDelivery = request.isDelivery,
            items = request.items
        )
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(OrderResponse.fromEntity(createdOrder))
    }

    /**
     * Adiciona itens a um pedido existente
     */
    @PostMapping("/{id}/items")
    fun addItemsToOrder(
        @PathVariable id: Long,
        @Valid @RequestBody request: AddOrderItemsRequest
    ): ResponseEntity<OrderResponse> {
        val updatedOrder = orderService.addItemsToOrder(id, request.items)
        return ResponseEntity.ok(OrderResponse.fromEntity(updatedOrder))
    }

    /**
     * Atualiza o status de um pedido
     */
    @PatchMapping("/{id}/status")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: OrderStatusUpdateRequest
    ): ResponseEntity<OrderResponse> {
        val updatedOrder = orderService.updateStatus(id, request.status)
        return ResponseEntity.ok(OrderResponse.fromEntity(updatedOrder))
    }

    /**
     * Cancela um pedido
     */
    @PostMapping("/{id}/cancel")
    fun cancelOrder(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        val canceledOrder = orderService.cancelOrder(id)
        return ResponseEntity.ok(OrderResponse.fromEntity(canceledOrder))
    }
} 