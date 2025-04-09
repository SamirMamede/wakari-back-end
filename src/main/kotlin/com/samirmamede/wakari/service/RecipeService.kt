package com.samirmamede.wakari.service

import com.samirmamede.wakari.model.Recipe
import com.samirmamede.wakari.repository.MenuItemRepository
import com.samirmamede.wakari.repository.RecipeRepository
import com.samirmamede.wakari.repository.StockItemRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val menuItemRepository: MenuItemRepository,
    private val stockItemRepository: StockItemRepository,
    private val menuItemService: MenuItemService
) {
    fun findAll(): List<Recipe> = recipeRepository.findAll()
    
    fun findById(id: Long): Recipe = recipeRepository.findById(id)
        .orElseThrow { EntityNotFoundException("Receita não encontrada com ID: $id") }
    
    fun findByMenuItemId(menuItemId: Long): List<Recipe> {
        val menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow { EntityNotFoundException("Item do cardápio não encontrado com ID: $menuItemId") }
        return recipeRepository.findByMenuItem(menuItem)
    }
    
    fun findByStockItemId(stockItemId: Long): List<Recipe> {
        val stockItem = stockItemRepository.findById(stockItemId)
            .orElseThrow { EntityNotFoundException("Item de estoque não encontrado com ID: $stockItemId") }
        return recipeRepository.findByStockItem(stockItem)
    }
    
    @Transactional
    fun create(recipe: Recipe): Recipe {
        // Verificar se o item do cardápio existe
        val menuItem = menuItemRepository.findById(recipe.menuItem.id)
            .orElseThrow { EntityNotFoundException("Item do cardápio não encontrado com ID: ${recipe.menuItem.id}") }
        
        // Verificar se o item de estoque existe
        val stockItem = stockItemRepository.findById(recipe.stockItem.id)
            .orElseThrow { EntityNotFoundException("Item de estoque não encontrado com ID: ${recipe.stockItem.id}") }
        
        // Verificar se já existe uma receita para este item do cardápio e item de estoque
        val existingRecipe = recipeRepository.findByMenuItemIdAndStockItemId(menuItem.id, stockItem.id)
        if (existingRecipe != null) {
            throw IllegalStateException("Já existe uma receita para este item do cardápio com este ingrediente")
        }
        
        // Criar a nova receita
        val newRecipe = Recipe(
            menuItem = menuItem,
            stockItem = stockItem,
            quantity = recipe.quantity
        )
        
        val savedRecipe = recipeRepository.save(newRecipe)
        
        // Atualizar disponibilidade do item do cardápio
        menuItemService.updateAvailabilityBasedOnStock(menuItem)
        
        return savedRecipe
    }
    
    @Transactional
    fun update(id: Long, updatedRecipe: Recipe): Recipe {
        val existingRecipe = findById(id)
        
        // Atualizar apenas a quantidade, não permitindo alterar o item do cardápio ou o item de estoque
        existingRecipe.quantity = updatedRecipe.quantity
        existingRecipe.updatedAt = LocalDateTime.now()
        
        val savedRecipe = recipeRepository.save(existingRecipe)
        
        // Atualizar disponibilidade do item do cardápio
        menuItemService.updateAvailabilityBasedOnStock(existingRecipe.menuItem)
        
        return savedRecipe
    }
    
    @Transactional
    fun delete(id: Long) {
        val recipe = findById(id)
        val menuItem = recipe.menuItem
        
        recipeRepository.delete(recipe)
        
        // Atualizar disponibilidade do item do cardápio
        menuItemService.updateAvailabilityBasedOnStock(menuItem)
    }
} 