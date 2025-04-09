package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.Recipe
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

data class RecipeRequest(
    @field:NotNull(message = "ID do item do cardápio é obrigatório")
    val menuItemId: Long,
    
    @field:NotNull(message = "ID do item de estoque é obrigatório")
    val stockItemId: Long,
    
    @field:NotNull(message = "Quantidade é obrigatória")
    @field:Positive(message = "Quantidade deve ser maior que zero")
    val quantity: BigDecimal
)

data class RecipeUpdateRequest(
    @field:NotNull(message = "Quantidade é obrigatória")
    @field:Positive(message = "Quantidade deve ser maior que zero")
    val quantity: BigDecimal
)

data class RecipeResponse(
    val id: Long,
    val menuItem: MenuItemSimpleResponse,
    val stockItem: StockItemResponse,
    val quantity: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(recipe: Recipe): RecipeResponse {
            return RecipeResponse(
                id = recipe.id,
                menuItem = MenuItemSimpleResponse.fromEntity(recipe.menuItem),
                stockItem = StockItemResponse.fromEntity(recipe.stockItem),
                quantity = recipe.quantity,
                createdAt = recipe.createdAt,
                updatedAt = recipe.updatedAt
            )
        }
    }
}

data class RecipeSimpleResponse(
    val id: Long,
    val stockItemId: Long,
    val stockItemName: String,
    val quantity: BigDecimal,
    val unit: String
) {
    companion object {
        fun fromEntity(recipe: Recipe): RecipeSimpleResponse {
            return RecipeSimpleResponse(
                id = recipe.id,
                stockItemId = recipe.stockItem.id,
                stockItemName = recipe.stockItem.name,
                quantity = recipe.quantity,
                unit = recipe.stockItem.unit
            )
        }
    }
} 