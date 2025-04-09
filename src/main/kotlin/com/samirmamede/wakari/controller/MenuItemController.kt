package com.samirmamede.wakari.controller

import com.samirmamede.wakari.dto.*
import com.samirmamede.wakari.model.MenuItem
import com.samirmamede.wakari.model.Recipe
import com.samirmamede.wakari.service.MenuItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/menu")
@Tag(name = "Cardápio", description = "Endpoints para gerenciamento de itens do cardápio")
class MenuItemController(private val menuItemService: MenuItemService) {

    @GetMapping
    @Operation(summary = "Listar todos os itens do cardápio")
    fun getAllMenuItems(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort: String
    ): ResponseEntity<Page<MenuItemResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(sort))
        val items = menuItemService.findAll(pageable)
        return ResponseEntity.ok(items.map { MenuItemResponse.fromEntity(it) })
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar item do cardápio por ID")
    fun getMenuItemById(@PathVariable id: Long): ResponseEntity<MenuItemResponse> {
        val item = menuItemService.findById(id)
        return ResponseEntity.ok(MenuItemResponse.fromEntity(item))
    }
    
    @GetMapping("/available")
    @Operation(summary = "Listar itens disponíveis no cardápio")
    fun getAvailableMenuItems(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort: String
    ): ResponseEntity<Page<MenuItemResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(sort))
        val items = menuItemService.findByAvailable(true, pageable)
        return ResponseEntity.ok(items.map { MenuItemResponse.fromEntity(it) })
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Listar itens do cardápio por categoria")
    fun getMenuItemsByCategory(
        @PathVariable category: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort: String
    ): ResponseEntity<Page<MenuItemResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(sort))
        val items = menuItemService.findByCategory(category, pageable)
        return ResponseEntity.ok(items.map { MenuItemResponse.fromEntity(it) })
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Listar todas as categorias do cardápio")
    fun getAllCategories(): ResponseEntity<List<String>> {
        val categories = menuItemService.findAllCategories()
        return ResponseEntity.ok(categories)
    }
    
    @PostMapping
    @Operation(summary = "Criar novo item do cardápio")
    @PreAuthorize("hasRole('ADMIN')")
    fun createMenuItem(@Valid @RequestBody request: MenuItemRequest): ResponseEntity<MenuItemResponse> {
        val newItem = MenuItem(
            name = request.name,
            description = request.description,
            price = request.price,
            available = true, // Por padrão, o novo item é disponível
            imageUrl = request.imageUrl,
            category = request.category
        )
        
        val createdItem = menuItemService.create(newItem)
        return ResponseEntity.status(HttpStatus.CREATED).body(MenuItemResponse.fromEntity(createdItem))
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item do cardápio")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateMenuItem(
        @PathVariable id: Long,
        @Valid @RequestBody request: MenuItemUpdateRequest
    ): ResponseEntity<MenuItemResponse> {
        val updatedItem = MenuItem(
            id = id,
            name = request.name,
            description = request.description,
            price = request.price,
            available = request.available,
            imageUrl = request.imageUrl,
            category = request.category
        )
        
        val saved = menuItemService.update(id, updatedItem)
        return ResponseEntity.ok(MenuItemResponse.fromEntity(saved))
    }
    
    @PostMapping("/{id}/recipes")
    @Operation(summary = "Adicionar ingrediente (receita) a um item do cardápio")
    @PreAuthorize("hasRole('ADMIN')")
    fun addRecipe(
        @PathVariable id: Long,
        @Valid @RequestBody request: RecipeRequest
    ): ResponseEntity<MenuItemResponse> {
        // Criamos um objeto Recipe com dados mínimos necessários para o service resolver
        val recipeToAdd = Recipe(
            menuItem = MenuItem(id = id),
            stockItem = com.samirmamede.wakari.model.StockItem(id = request.stockItemId),
            quantity = request.quantity
        )
        
        val updatedMenuItem = menuItemService.addRecipe(id, recipeToAdd)
        return ResponseEntity.ok(MenuItemResponse.fromEntity(updatedMenuItem))
    }
    
    @PutMapping("/{menuItemId}/recipes/{recipeId}")
    @Operation(summary = "Atualizar quantidade de um ingrediente na receita")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateRecipe(
        @PathVariable menuItemId: Long,
        @PathVariable recipeId: Long,
        @Valid @RequestBody request: RecipeUpdateRequest
    ): ResponseEntity<RecipeResponse> {
        val updatedRecipe = menuItemService.updateRecipe(menuItemId, recipeId, request.quantity)
        return ResponseEntity.ok(RecipeResponse.fromEntity(updatedRecipe))
    }
    
    @DeleteMapping("/{menuItemId}/recipes/{recipeId}")
    @Operation(summary = "Remover ingrediente da receita de um item do cardápio")
    @PreAuthorize("hasRole('ADMIN')")
    fun removeRecipe(
        @PathVariable menuItemId: Long,
        @PathVariable recipeId: Long
    ): ResponseEntity<Void> {
        menuItemService.removeRecipe(menuItemId, recipeId)
        return ResponseEntity.noContent().build()
    }
    
    @PostMapping("/update-availability")
    @Operation(summary = "Atualizar a disponibilidade de todos os itens do cardápio com base no estoque")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun updateAllAvailability(): ResponseEntity<Void> {
        menuItemService.updateAllAvailability()
        return ResponseEntity.ok().build()
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir item do cardápio")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteMenuItem(@PathVariable id: Long): ResponseEntity<Void> {
        menuItemService.delete(id)
        return ResponseEntity.noContent().build()
    }
} 