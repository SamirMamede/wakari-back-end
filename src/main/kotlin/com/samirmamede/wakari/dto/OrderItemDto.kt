package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.OrderItem
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

// DTO para criação de item de pedido
data class OrderItemRequest(
    @field:NotNull(message = "ID do item do cardápio é obrigatório")
    val menuItemId: Long,
    
    @field:NotNull(message = "Quantidade é obrigatória")
    @field:Positive(message = "Quantidade deve ser maior que zero")
    val quantity: Int,
    
    val notes: String? = null
)

// DTO para atualização de quantidade de item do pedido
data class OrderItemUpdateRequest(
    @field:NotNull(message = "Quantidade é obrigatória")
    @field:Positive(message = "Quantidade deve ser maior que zero")
    val quantity: Int,
    
    val notes: String? = null
)

// DTO para resposta de item de pedido
data class OrderItemResponse(
    val id: Long,
    val menuItemId: Long,
    val menuItemName: String,
    val quantity: Int,
    val priceAtTime: BigDecimal,
    val subtotal: BigDecimal,
    val notes: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(orderItem: OrderItem): OrderItemResponse {
            return OrderItemResponse(
                id = orderItem.id,
                menuItemId = orderItem.menuItem.id,
                menuItemName = orderItem.menuItem.name,
                quantity = orderItem.quantity,
                priceAtTime = orderItem.priceAtTime,
                subtotal = orderItem.priceAtTime.multiply(orderItem.quantity.toBigDecimal()),
                notes = orderItem.notes,
                createdAt = orderItem.createdAt,
                updatedAt = orderItem.updatedAt
            )
        }
    }
} 