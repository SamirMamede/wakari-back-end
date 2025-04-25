package com.samirmamede.wakari.controller

import com.samirmamede.wakari.dto.OrderItemResponse
import com.samirmamede.wakari.dto.OrderItemUpdateRequest
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.repository.OrderItemRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/order-items")
class OrderItemController(private val orderItemRepository: OrderItemRepository) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<OrderItemResponse> {
        val orderItem = orderItemRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Item de pedido não encontrado com ID: $id") }
        return ResponseEntity.ok(OrderItemResponse.fromEntity(orderItem))
    }

    @GetMapping("/order/{orderId}")
    fun findByOrderId(@PathVariable orderId: Long): ResponseEntity<List<OrderItemResponse>> {
        val items = orderItemRepository.findByOrderId(orderId)
            .map { OrderItemResponse.fromEntity(it) }
        return ResponseEntity.ok(items)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: OrderItemUpdateRequest
    ): ResponseEntity<OrderItemResponse> {
        val orderItem = orderItemRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Item de pedido não encontrado com ID: $id") }
        
        // Atualiza os valores
        orderItem.quantity = request.quantity
        request.notes?.let { orderItem.notes = it }
        
        // Salva as alterações
        val updatedItem = orderItemRepository.save(orderItem)
        
        // Recalcula o total do pedido
        val order = updatedItem.order
        order.calculateTotal()
        
        return ResponseEntity.ok(OrderItemResponse.fromEntity(updatedItem))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val orderItem = orderItemRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Item de pedido não encontrado com ID: $id") }
        
        // Guarda a referência ao pedido antes de excluir o item
        val order = orderItem.order
        
        // Remove o item
        orderItemRepository.deleteById(id)
        
        // Recalcula o total do pedido
        order.calculateTotal()
        
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
} 