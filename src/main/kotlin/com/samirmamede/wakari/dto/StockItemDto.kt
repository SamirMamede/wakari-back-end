package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.StockItem
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal
import java.time.LocalDateTime

data class StockItemRequest(
    @field:NotBlank(message = "Nome do item é obrigatório")
    val name: String,
    
    @field:NotNull(message = "Quantidade é obrigatória")
    @field:PositiveOrZero(message = "Quantidade deve ser maior ou igual a zero")
    val quantity: BigDecimal,
    
    @field:NotBlank(message = "Unidade de medida é obrigatória")
    val unit: String,
    
    @field:NotNull(message = "Quantidade mínima é obrigatória")
    @field:PositiveOrZero(message = "Quantidade mínima deve ser maior ou igual a zero")
    val minQuantity: BigDecimal
)

data class StockItemUpdateRequest(
    @field:NotBlank(message = "Nome do item é obrigatório")
    val name: String,
    
    @field:NotNull(message = "Quantidade é obrigatória")
    @field:PositiveOrZero(message = "Quantidade deve ser maior ou igual a zero")
    val quantity: BigDecimal,
    
    @field:NotBlank(message = "Unidade de medida é obrigatória")
    val unit: String,
    
    @field:NotNull(message = "Quantidade mínima é obrigatória")
    @field:PositiveOrZero(message = "Quantidade mínima deve ser maior ou igual a zero")
    val minQuantity: BigDecimal
)

data class StockItemQuantityRequest(
    @field:NotNull(message = "Quantidade é obrigatória")
    @field:Positive(message = "Quantidade deve ser maior que zero")
    val quantity: BigDecimal
)

data class StockItemResponse(
    val id: Long,
    val name: String,
    val quantity: BigDecimal,
    val unit: String,
    val minQuantity: BigDecimal,
    val isLowStock: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(stockItem: StockItem): StockItemResponse {
            return StockItemResponse(
                id = stockItem.id,
                name = stockItem.name,
                quantity = stockItem.quantity,
                unit = stockItem.unit,
                minQuantity = stockItem.minQuantity,
                isLowStock = stockItem.isLowStock(),
                createdAt = stockItem.createdAt,
                updatedAt = stockItem.updatedAt
            )
        }
    }
} 