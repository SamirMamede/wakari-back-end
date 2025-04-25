package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.Order
import com.samirmamede.wakari.model.OrderStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

// DTO para criação de pedido
data class OrderRequest(
    @field:NotNull(message = "ID do usuário é obrigatório")
    val userId: Long,
    
    // A mesa é opcional, pode ser null para delivery
    val tableId: Long? = null,
    
    @field:NotNull(message = "Deve informar se é delivery ou não")
    val isDelivery: Boolean = false,
    
    @field:Valid
    @field:NotEmpty(message = "O pedido deve conter pelo menos um item")
    val items: List<OrderItemRequest>
)

// DTO para adição de itens a um pedido existente
data class AddOrderItemsRequest(
    @field:Valid
    @field:NotEmpty(message = "Deve incluir pelo menos um item")
    val items: List<OrderItemRequest>
)

// DTO para atualização de status do pedido
data class OrderStatusUpdateRequest(
    @field:NotNull(message = "Status é obrigatório")
    val status: OrderStatus
)

// DTO para resposta de pedido
data class OrderResponse(
    val id: Long,
    val userId: Long,
    val userName: String,
    val tableId: Long?,
    val tableNumber: Int?,
    val total: BigDecimal,
    val status: OrderStatus,
    val isDelivery: Boolean,
    val items: List<OrderItemResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(order: Order, includeItems: Boolean = true): OrderResponse {
            return OrderResponse(
                id = order.id,
                userId = order.user.id,
                userName = order.user.name,
                tableId = order.table?.id,
                tableNumber = order.table?.number,
                total = order.total,
                status = order.status,
                isDelivery = order.isDelivery,
                items = if (includeItems) order.items.map { OrderItemResponse.fromEntity(it) } else emptyList(),
                createdAt = order.createdAt,
                updatedAt = order.updatedAt
            )
        }
    }
} 