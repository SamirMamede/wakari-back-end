package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.Recipe
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

@Schema(
    description = "Requisição para criar uma nova receita",
    example = """
        {
          "menuItemId": 1,
          "stockItemId": 1,
          "quantity": 0.5,
          "cost": 5.00
        }
    """
)
data class RecipeRequest(
    @field:NotNull(message = "ID do item do cardápio é obrigatório")
    @Schema(
        description = "ID do item do cardápio que terá a receita criada",
        example = "1",
        required = true,
        minimum = "1"
    )
    val menuItemId: Long,
    
    @field:NotNull(message = "ID do item de estoque é obrigatório")
    @Schema(
        description = "ID do item de estoque que será usado na receita",
        example = "1",
        required = true,
        minimum = "1"
    )
    val stockItemId: Long,
    
    @field:NotNull(message = "Quantidade é obrigatória")
    @field:Positive(message = "Quantidade deve ser maior que zero")
    @Schema(
        description = "Quantidade do item de estoque necessária para a receita. Deve ser maior que zero e compatível com a unidade de medida do item de estoque",
        example = "0.5",
        required = true,
        minimum = "0.01"
    )
    val quantity: BigDecimal,

    @field:NotNull(message = "Custo é obrigatório")
    @field:Positive(message = "Custo deve ser maior que zero")
    @Schema(
        description = "Custo do item de estoque para esta receita",
        example = "5.00",
        required = true,
        minimum = "0.01"
    )
    val cost: BigDecimal
)

@Schema(
    description = "Requisição para atualizar a quantidade e o custo de um ingrediente na receita",
    example = """
        {
          "quantity": 0.75,
          "cost": 6.00
        }
    """
)
data class RecipeUpdateRequest(
    @field:NotNull(message = "Quantidade é obrigatória")
    @field:Positive(message = "Quantidade deve ser maior que zero")
    @Schema(
        description = "Nova quantidade do item de estoque na receita. Deve ser maior que zero e compatível com a unidade de medida do item de estoque",
        example = "0.75",
        required = true,
        minimum = "0.01"
    )
    val quantity: BigDecimal,

    @field:NotNull(message = "Custo é obrigatório")
    @field:Positive(message = "Custo deve ser maior que zero")
    @Schema(
        description = "Novo custo do item de estoque para esta receita",
        example = "6.00",
        required = true,
        minimum = "0.01"
    )
    val cost: BigDecimal
)

@Schema(
    description = "Resposta detalhada de uma receita",
    example = """
        {
          "id": 1,
          "menuItemId": 1,
          "stockItemId": 1,
          "quantity": 0.5,
          "cost": 5.00,
          "createdAt": "2024-01-20T10:30:00",
          "updatedAt": "2024-01-20T10:30:00"
        }
    """
)
data class RecipeResponse(
    @Schema(
        description = "ID único da receita",
        example = "1"
    )
    val id: Long,
    
    @Schema(
        description = "ID do item do cardápio ao qual a receita pertence",
        example = "1"
    )
    val menuItemId: Long,
    
    @Schema(
        description = "ID do item de estoque utilizado na receita",
        example = "1"
    )
    val stockItemId: Long,
    
    @Schema(
        description = "Quantidade do item de estoque necessária para preparar o item do cardápio",
        example = "0.5"
    )
    val quantity: BigDecimal,

    @Schema(
        description = "Custo do item de estoque para esta receita",
        example = "5.00"
    )
    val cost: BigDecimal,
    
    @Schema(
        description = "Data e hora de criação da receita",
        example = "2024-01-20T10:30:00",
        format = "date-time"
    )
    val createdAt: LocalDateTime,
    
    @Schema(
        description = "Data e hora da última atualização da receita",
        example = "2024-01-20T10:30:00",
        format = "date-time"
    )
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(recipe: Recipe): RecipeResponse {
            return RecipeResponse(
                id = recipe.id,
                menuItemId = recipe.menuItem.id,
                stockItemId = recipe.stockItem.id,
                quantity = recipe.quantity,
                cost = recipe.cost,
                createdAt = recipe.createdAt,
                updatedAt = recipe.updatedAt
            )
        }
    }
}

@Schema(
    description = "Resposta simplificada de uma receita, contendo apenas as informações essenciais",
    example = """
        {
          "id": 1,
          "stockItemId": 1,
          "stockItemName": "Farinha de Trigo",
          "quantity": 0.5,
          "cost": 5.00,
          "unit": "kg"
        }
    """
)
data class RecipeSimpleResponse(
    @Schema(
        description = "ID único da receita",
        example = "1"
    )
    val id: Long,
    
    @Schema(
        description = "ID do item de estoque utilizado na receita",
        example = "1"
    )
    val stockItemId: Long,
    
    @Schema(
        description = "Nome do item de estoque utilizado na receita",
        example = "Farinha de Trigo"
    )
    val stockItemName: String,
    
    @Schema(
        description = "Quantidade do item de estoque necessária para preparar o item do cardápio",
        example = "0.5"
    )
    val quantity: BigDecimal,

    @Schema(
        description = "Custo do item de estoque para esta receita",
        example = "5.00"
    )
    val cost: BigDecimal,
    
    @Schema(
        description = "Unidade de medida do item de estoque (ex: kg, g, l, ml, un)",
        example = "kg"
    )
    val unit: String
) {
    companion object {
        fun fromEntity(recipe: Recipe): RecipeSimpleResponse {
            return RecipeSimpleResponse(
                id = recipe.id,
                stockItemId = recipe.stockItem.id,
                stockItemName = recipe.stockItem.name,
                quantity = recipe.quantity,
                cost = recipe.cost,
                unit = recipe.stockItem.unit
            )
        }
    }
} 