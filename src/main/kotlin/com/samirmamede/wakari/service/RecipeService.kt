package com.samirmamede.wakari.service

import com.samirmamede.wakari.dto.RecipeRequest
import com.samirmamede.wakari.dto.RecipeResponse
import com.samirmamede.wakari.dto.RecipeUpdateRequest
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.model.Recipe
import com.samirmamede.wakari.repository.MenuItemRepository
import com.samirmamede.wakari.repository.RecipeRepository
import com.samirmamede.wakari.repository.StockItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val menuItemRepository: MenuItemRepository,
    private val stockItemRepository: StockItemRepository
) {
    fun findAll(): List<RecipeResponse> = recipeRepository.findAll().map { RecipeResponse.fromEntity(it) }
    
    fun findById(id: Long): RecipeResponse {
        val recipe = recipeRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Receita não encontrada com ID: $id") }
        return RecipeResponse.fromEntity(recipe)
    }
    
    fun findByMenuItemId(menuItemId: Long): List<RecipeResponse> {
        val menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { ResourceNotFoundException("Item do cardápio não encontrado com ID: $menuItemId") }
        return recipeRepository.findByMenuItemId(menuItemId).map { RecipeResponse.fromEntity(it) }
    }
    
    fun findByStockItemId(stockItemId: Long): List<RecipeResponse> {
        val stockItem = stockItemRepository.findById(stockItemId)
            .orElseThrow { ResourceNotFoundException("Item de estoque não encontrado com ID: $stockItemId") }
        return recipeRepository.findByStockItemId(stockItemId).map { RecipeResponse.fromEntity(it) }
    }
    
    @Transactional
    fun create(request: RecipeRequest): RecipeResponse {
        // Verificar se o item do cardápio existe
        val menuItem = menuItemRepository.findById(request.menuItemId)
            .orElseThrow { ResourceNotFoundException("Item do cardápio não encontrado com ID: ${request.menuItemId}") }
        
        // Verificar se o item de estoque existe
        val stockItem = stockItemRepository.findById(request.stockItemId)
            .orElseThrow { ResourceNotFoundException("Item de estoque não encontrado com ID: ${request.stockItemId}") }
        
        // Verificar se já existe uma receita para este item do cardápio e item de estoque
        val existingRecipe = recipeRepository.findByMenuItemIdAndStockItemId(menuItem.id, stockItem.id)
        if (existingRecipe != null) {
            throw IllegalStateException("Já existe uma receita para este item do cardápio com este ingrediente")
        }
        
        // Criar a nova receita
        val newRecipe = Recipe(
            menuItem = menuItem,
            stockItem = stockItem,
            quantity = request.quantity,
            cost = request.cost
        )
        
        val savedRecipe = recipeRepository.save(newRecipe)
        return RecipeResponse.fromEntity(savedRecipe)
    }
    
    @Transactional
    fun update(id: Long, request: RecipeUpdateRequest): RecipeResponse {
        val existingRecipe = recipeRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Receita não encontrada com ID: $id") }
        
        // Atualizar apenas a quantidade e o custo
        val updatedRecipe = existingRecipe.copy(
            quantity = request.quantity,
            cost = request.cost
        )
        
        val savedRecipe = recipeRepository.save(updatedRecipe)
        return RecipeResponse.fromEntity(savedRecipe)
    }
    
    @Transactional
    fun delete(id: Long) {
        val recipe = recipeRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Receita não encontrada com ID: $id") }
        recipeRepository.deleteById(recipe.id)
    }
} 