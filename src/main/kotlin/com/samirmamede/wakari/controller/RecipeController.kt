package com.samirmamede.wakari.controller

import com.samirmamede.wakari.dto.RecipeRequest
import com.samirmamede.wakari.dto.RecipeResponse
import com.samirmamede.wakari.dto.RecipeUpdateRequest
import com.samirmamede.wakari.model.MenuItem
import com.samirmamede.wakari.model.Recipe
import com.samirmamede.wakari.model.StockItem
import com.samirmamede.wakari.service.RecipeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/recipes")
@Tag(name = "Receitas", description = "Endpoints para gerenciamento de receitas (relação entre itens do cardápio e itens de estoque)")
class RecipeController(private val recipeService: RecipeService) {

    @GetMapping
    @Operation(summary = "Listar todas as receitas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getAllRecipes(): ResponseEntity<List<RecipeResponse>> {
        val recipes = recipeService.findAll()
        return ResponseEntity.ok(recipes.map { RecipeResponse.fromEntity(it) })
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar receita por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<RecipeResponse> {
        val recipe = recipeService.findById(id)
        return ResponseEntity.ok(RecipeResponse.fromEntity(recipe))
    }
    
    @GetMapping("/menu-item/{menuItemId}")
    @Operation(summary = "Listar receitas por item do cardápio")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getRecipesByMenuItem(@PathVariable menuItemId: Long): ResponseEntity<List<RecipeResponse>> {
        val recipes = recipeService.findByMenuItemId(menuItemId)
        return ResponseEntity.ok(recipes.map { RecipeResponse.fromEntity(it) })
    }
    
    @GetMapping("/stock-item/{stockItemId}")
    @Operation(summary = "Listar receitas por item de estoque")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getRecipesByStockItem(@PathVariable stockItemId: Long): ResponseEntity<List<RecipeResponse>> {
        val recipes = recipeService.findByStockItemId(stockItemId)
        return ResponseEntity.ok(recipes.map { RecipeResponse.fromEntity(it) })
    }
    
    @PostMapping
    @Operation(summary = "Criar nova receita")
    @PreAuthorize("hasRole('ADMIN')")
    fun createRecipe(@Valid @RequestBody request: RecipeRequest): ResponseEntity<RecipeResponse> {
        val newRecipe = Recipe(
            menuItem = MenuItem(id = request.menuItemId),
            stockItem = StockItem(id = request.stockItemId),
            quantity = request.quantity
        )
        
        val createdRecipe = recipeService.create(newRecipe)
        return ResponseEntity.status(HttpStatus.CREATED).body(RecipeResponse.fromEntity(createdRecipe))
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar receita")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateRecipe(
        @PathVariable id: Long,
        @Valid @RequestBody request: RecipeUpdateRequest
    ): ResponseEntity<RecipeResponse> {
        // Criamos um objeto Recipe apenas com a quantidade, já que é o único campo que pode ser atualizado
        val updatedRecipe = Recipe(
            id = id,
            menuItem = MenuItem(id = 0), // Valor dummy, não será usado
            stockItem = StockItem(id = 0), // Valor dummy, não será usado
            quantity = request.quantity
        )
        
        val saved = recipeService.update(id, updatedRecipe)
        return ResponseEntity.ok(RecipeResponse.fromEntity(saved))
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir receita")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<Void> {
        recipeService.delete(id)
        return ResponseEntity.noContent().build()
    }
} 