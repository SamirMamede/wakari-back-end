package com.samirmamede.wakari.controller

import com.samirmamede.wakari.dto.*
import com.samirmamede.wakari.model.RestaurantTable
import com.samirmamede.wakari.model.TableStatus
import com.samirmamede.wakari.service.TableService
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
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/tables")
@Tag(name = "Mesas", description = "Endpoints para gerenciamento de mesas do restaurante")
class TableController(private val tableService: TableService) {

    @GetMapping
    @Operation(summary = "Listar todas as mesas")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    fun getAllTables(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "number") sort: String
    ): ResponseEntity<Page<TableResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(sort))
        val tables = tableService.findAll(pageable)
        return ResponseEntity.ok(tables.map { TableResponse.fromEntity(it) })
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar mesa por ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    fun getTableById(@PathVariable id: Long): ResponseEntity<TableResponse> {
        val table = tableService.findById(id)
        return ResponseEntity.ok(TableResponse.fromEntity(table))
    }
    
    @GetMapping("/number/{number}")
    @Operation(summary = "Buscar mesa por número")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    fun getTableByNumber(@PathVariable number: Int): ResponseEntity<TableResponse> {
        val table = tableService.findByNumber(number)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(TableResponse.fromEntity(table))
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Listar mesas por status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    fun getTablesByStatus(@PathVariable status: TableStatus): ResponseEntity<List<TableResponse>> {
        val tables = tableService.findByStatus(status)
        return ResponseEntity.ok(tables.map { TableResponse.fromEntity(it) })
    }
    
    @PostMapping
    @Operation(summary = "Criar nova mesa")
    @PreAuthorize("hasRole('ADMIN')")
    fun createTable(@Valid @RequestBody request: TableRequest): ResponseEntity<TableResponse> {
        val table = RestaurantTable(
            number = request.number,
            capacity = request.capacity,
            status = TableStatus.AVAILABLE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val createdTable = tableService.create(table)
        return ResponseEntity.status(HttpStatus.CREATED).body(TableResponse.fromEntity(createdTable))
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar mesa existente")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateTable(
        @PathVariable id: Long,
        @Valid @RequestBody request: TableUpdateRequest
    ): ResponseEntity<TableResponse> {
        val table = RestaurantTable(
            id = id,
            number = request.number,
            capacity = request.capacity,
            status = request.status,
            updatedAt = LocalDateTime.now()
        )
        val updatedTable = tableService.update(id, table)
        return ResponseEntity.ok(TableResponse.fromEntity(updatedTable))
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da mesa")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    fun updateTableStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: TableStatusUpdateRequest
    ): ResponseEntity<TableResponse> {
        val updatedTable = tableService.updateStatus(id, request.status)
        return ResponseEntity.ok(TableResponse.fromEntity(updatedTable))
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir mesa")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteTable(@PathVariable id: Long): ResponseEntity<Void> {
        tableService.delete(id)
        return ResponseEntity.noContent().build()
    }
    
    @PostMapping("/cleanup")
    @Operation(summary = "Limpar todas as mesas em estado de limpeza")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    fun cleanupTables(): ResponseEntity<Void> {
        tableService.cleanupTables()
        return ResponseEntity.ok().build()
    }
} 