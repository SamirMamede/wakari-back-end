package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.MenuItem
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

data class MenuItemRequest(
    @field:NotBlank(message = "Nome do item é obrigatório")
    val name: String,
    
    val description: String?,
    
    @field:NotNull(message = "Preço é obrigatório")
    @field:Positive(message = "Preço deve ser maior que zero")
    val price: BigDecimal,
    
    val imageUrl: String?,
    
    @field:NotBlank(message = "Categoria é obrigatória")
    val category: String
)

data class MenuItemUpdateRequest(
    @field:NotBlank(message = "Nome do item é obrigatório")
    val name: String,
    
    val description: String?,
    
    @field:NotNull(message = "Preço é obrigatório")
    @field:Positive(message = "Preço deve ser maior que zero")
    val price: BigDecimal,
    
    @field:NotNull(message = "Disponibilidade é obrigatória")
    val available: Boolean,
    
    val imageUrl: String?,
    
    @field:NotBlank(message = "Categoria é obrigatória")
    val category: String
)

data class MenuItemResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val available: Boolean,
    val imageUrl: String?,
    val category: String,
    val recipes: List<RecipeSimpleResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(menuItem: MenuItem): MenuItemResponse {
            return MenuItemResponse(
                id = menuItem.id,
                name = menuItem.name,
                description = menuItem.description,
                price = menuItem.price,
                available = menuItem.available,
                imageUrl = menuItem.imageUrl,
                category = menuItem.category,
                recipes = menuItem.recipes.map { RecipeSimpleResponse.fromEntity(it) },
                createdAt = menuItem.createdAt,
                updatedAt = menuItem.updatedAt
            )
        }
    }
}

data class MenuItemSimpleResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val available: Boolean,
    val imageUrl: String?,
    val category: String
) {
    companion object {
        fun fromEntity(menuItem: MenuItem): MenuItemSimpleResponse {
            return MenuItemSimpleResponse(
                id = menuItem.id,
                name = menuItem.name,
                description = menuItem.description,
                price = menuItem.price,
                available = menuItem.available,
                imageUrl = menuItem.imageUrl,
                category = menuItem.category
            )
        }
    }
} 