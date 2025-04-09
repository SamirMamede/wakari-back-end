package com.samirmamede.wakari.controller

import com.samirmamede.wakari.dto.*
import com.samirmamede.wakari.model.StockItem
import com.samirmamede.wakari.service.StockItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/stock")
@Tag(name = "Estoque", description = "Endpoints para gerenciamento de itens de estoque")
class StockItemController(private val stockItemService: StockItemService) {

    @GetMapping
    @Operation(summary = "Listar todos os itens do estoque")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getAllStockItems(): ResponseEntity<List<StockItemResponse>> {
        val items = stockItemService.findAll()
        return ResponseEntity.ok(items.map { StockItemResponse.fromEntity(it) })
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar item do estoque por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getStockItemById(@PathVariable id: Long): ResponseEntity<StockItemResponse> {
        val item = stockItemService.findById(id)
        return ResponseEntity.ok(StockItemResponse.fromEntity(item))
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar itens do estoque por nome")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun searchStockItems(@RequestParam name: String): ResponseEntity<List<StockItemResponse>> {
        val items = stockItemService.findByName(name)
        return ResponseEntity.ok(items.map { StockItemResponse.fromEntity(it) })
    }
    
    @GetMapping("/low-stock")
    @Operation(summary = "Listar itens com estoque baixo")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getLowStockItems(): ResponseEntity<List<StockItemResponse>> {
        val items = stockItemService.findAllLowStock()
        return ResponseEntity.ok(items.map { StockItemResponse.fromEntity(it) })
    }
    
    @PostMapping
    @Operation(summary = "Criar novo item de estoque")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun createStockItem(@Valid @RequestBody request: StockItemRequest): ResponseEntity<StockItemResponse> {
        val newItem = StockItem(
            name = request.name,
            quantity = request.quantity,
            unit = request.unit,
            minQuantity = request.minQuantity
        )
        
        val createdItem = stockItemService.create(newItem)
        return ResponseEntity.status(HttpStatus.CREATED).body(StockItemResponse.fromEntity(createdItem))
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item de estoque")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun updateStockItem(
        @PathVariable id: Long,
        @Valid @RequestBody request: StockItemUpdateRequest
    ): ResponseEntity<StockItemResponse> {
        val updatedItem = StockItem(
            id = id,
            name = request.name,
            quantity = request.quantity,
            unit = request.unit,
            minQuantity = request.minQuantity
        )
        
        val saved = stockItemService.update(id, updatedItem)
        return ResponseEntity.ok(StockItemResponse.fromEntity(saved))
    }
    
    @PatchMapping("/{id}/quantity")
    @Operation(summary = "Atualizar quantidade de um item no estoque")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun updateStockItemQuantity(
        @PathVariable id: Long,
        @Valid @RequestBody request: StockItemQuantityRequest
    ): ResponseEntity<StockItemResponse> {
        val updated = stockItemService.updateQuantity(id, request.quantity)
        return ResponseEntity.ok(StockItemResponse.fromEntity(updated))
    }
    
    @PostMapping("/{id}/add")
    @Operation(summary = "Adicionar quantidade a um item do estoque")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun addToStock(
        @PathVariable id: Long,
        @Valid @RequestBody request: StockItemQuantityRequest
    ): ResponseEntity<StockItemResponse> {
        val updated = stockItemService.addToStock(id, request.quantity)
        return ResponseEntity.ok(StockItemResponse.fromEntity(updated))
    }
    
    @PostMapping("/{id}/remove")
    @Operation(summary = "Remover quantidade de um item do estoque")
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun removeFromStock(
        @PathVariable id: Long,
        @Valid @RequestBody request: StockItemQuantityRequest
    ): ResponseEntity<StockItemResponse> {
        val updated = stockItemService.removeFromStock(id, request.quantity)
        return ResponseEntity.ok(StockItemResponse.fromEntity(updated))
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir item do estoque")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteStockItem(@PathVariable id: Long): ResponseEntity<Void> {
        stockItemService.delete(id)
        return ResponseEntity.noContent().build()
    }
} 