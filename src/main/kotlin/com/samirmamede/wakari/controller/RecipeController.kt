package com.samirmamede.wakari.controller

import com.samirmamede.wakari.dto.RecipeRequest
import com.samirmamede.wakari.dto.RecipeResponse
import com.samirmamede.wakari.dto.RecipeUpdateRequest
import com.samirmamede.wakari.model.MenuItem
import com.samirmamede.wakari.model.Recipe
import com.samirmamede.wakari.model.StockItem
import com.samirmamede.wakari.service.RecipeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/recipes")
@Tag(name = "Receitas", description = "Endpoints para gerenciamento de receitas (relação entre itens do cardápio e itens de estoque)")
class RecipeController(private val recipeService: RecipeService) {

    @GetMapping
    @Operation(
        summary = "Listar todas as receitas",
        description = "Retorna uma lista com todas as receitas cadastradas no sistema"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Lista de receitas retornada com sucesso",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = RecipeResponse::class),
                    examples = [
                        ExampleObject(
                            name = "success",
                            value = """
                                [
                                  {
                                    "id": 1,
                                    "menuItem": {
                                      "id": 1,
                                      "name": "Pizza Margherita",
                                      "description": "Pizza tradicional italiana",
                                      "price": 45.90,
                                      "category": "Pizzas"
                                    },
                                    "stockItem": {
                                      "id": 1,
                                      "name": "Farinha de Trigo",
                                      "quantity": 50.0,
                                      "unit": "kg",
                                      "minQuantity": 10.0
                                    },
                                    "quantity": 0.5,
                                    "createdAt": "2024-01-20T10:30:00",
                                    "updatedAt": "2024-01-20T10:30:00"
                                  }
                                ]
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este recurso")
    ])
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getAllRecipes(): ResponseEntity<List<RecipeResponse>> {
        val recipes = recipeService.findAll()
        return ResponseEntity.ok(recipes)
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar receita por ID",
        description = "Retorna os detalhes de uma receita específica"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Receita encontrada com sucesso"),
        ApiResponse(responseCode = "404", description = "Receita não encontrada"),
        ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este recurso")
    ])
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<RecipeResponse> {
        val recipe = recipeService.findById(id)
        return ResponseEntity.ok(recipe)
    }
    
    @GetMapping("/menu-item/{menuItemId}")
    @Operation(
        summary = "Listar receitas por item do cardápio",
        description = "Retorna todas as receitas associadas a um item específico do cardápio"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de receitas retornada com sucesso"),
        ApiResponse(responseCode = "404", description = "Item do cardápio não encontrado"),
        ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este recurso")
    ])
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getRecipesByMenuItem(@PathVariable menuItemId: Long): ResponseEntity<List<RecipeResponse>> {
        val recipes = recipeService.findByMenuItemId(menuItemId)
        return ResponseEntity.ok(recipes)
    }
    
    @GetMapping("/stock-item/{stockItemId}")
    @Operation(
        summary = "Listar receitas por item de estoque",
        description = "Retorna todas as receitas que utilizam um item específico do estoque"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de receitas retornada com sucesso"),
        ApiResponse(responseCode = "404", description = "Item de estoque não encontrado"),
        ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este recurso")
    ])
    @PreAuthorize("hasAnyRole('ADMIN', 'COZINHA')")
    fun getRecipesByStockItem(@PathVariable stockItemId: Long): ResponseEntity<List<RecipeResponse>> {
        val recipes = recipeService.findByStockItemId(stockItemId)
        return ResponseEntity.ok(recipes)
    }
    
    @PostMapping
    @Operation(
        summary = "Criar nova receita",
        description = "Cria uma nova receita associando um item do cardápio a um item do estoque com uma quantidade específica"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Receita criada com sucesso",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = RecipeResponse::class),
                    examples = [
                        ExampleObject(
                            name = "success",
                            value = """
                                {
                                  "id": 1,
                                  "menuItem": {
                                    "id": 1,
                                    "name": "Pizza Margherita",
                                    "description": "Pizza tradicional italiana",
                                    "price": 45.90,
                                    "category": "Pizzas"
                                  },
                                  "stockItem": {
                                    "id": 1,
                                    "name": "Farinha de Trigo",
                                    "quantity": 50.0,
                                    "unit": "kg",
                                    "minQuantity": 10.0
                                  },
                                  "quantity": 0.5,
                                  "createdAt": "2024-01-20T10:30:00",
                                  "updatedAt": "2024-01-20T10:30:00"
                                }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Dados inválidos na requisição",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = [
                        ExampleObject(
                            name = "badRequest",
                            value = """
                                {
                                  "timestamp": "2024-01-20T10:30:00",
                                  "status": 400,
                                  "error": "Bad Request",
                                  "message": "Quantidade deve ser maior que zero",
                                  "path": "/api/v1/recipes"
                                }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Item do cardápio ou item de estoque não encontrado",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = [
                        ExampleObject(
                            name = "notFound",
                            value = """
                                {
                                  "timestamp": "2024-01-20T10:30:00",
                                  "status": 404,
                                  "error": "Not Found",
                                  "message": "Item do cardápio não encontrado com ID 1",
                                  "path": "/api/v1/recipes"
                                }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(responseCode = "409", description = "Já existe uma receita para este item do cardápio com este item de estoque"),
        ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este recurso")
    ])
    @PreAuthorize("hasRole('ADMIN')")
    fun createRecipe(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = RecipeRequest::class),
                    examples = [
                        ExampleObject(
                            name = "request",
                            value = """
                                {
                                  "menuItemId": 1,
                                  "stockItemId": 1,
                                  "quantity": 0.5
                                }
                            """
                        )
                    ]
                )
            ]
        )
        @Valid @RequestBody request: RecipeRequest
    ): ResponseEntity<RecipeResponse> {
        val recipe = recipeService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(recipe)
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar receita",
        description = "Atualiza a quantidade de um item de estoque em uma receita existente"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Receita atualizada com sucesso",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = RecipeResponse::class),
                    examples = [
                        ExampleObject(
                            name = "success",
                            value = """
                                {
                                  "id": 1,
                                  "menuItem": {
                                    "id": 1,
                                    "name": "Pizza Margherita",
                                    "description": "Pizza tradicional italiana",
                                    "price": 45.90,
                                    "category": "Pizzas"
                                  },
                                  "stockItem": {
                                    "id": 1,
                                    "name": "Farinha de Trigo",
                                    "quantity": 50.0,
                                    "unit": "kg",
                                    "minQuantity": 10.0
                                  },
                                  "quantity": 0.75,
                                  "createdAt": "2024-01-20T10:30:00",
                                  "updatedAt": "2024-01-20T10:35:00"
                                }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Dados inválidos na requisição",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = [
                        ExampleObject(
                            name = "badRequest",
                            value = """
                                {
                                  "timestamp": "2024-01-20T10:30:00",
                                  "status": 400,
                                  "error": "Bad Request",
                                  "message": "Quantidade deve ser maior que zero",
                                  "path": "/api/v1/recipes/1"
                                }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Receita não encontrada",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = [
                        ExampleObject(
                            name = "notFound",
                            value = """
                                {
                                  "timestamp": "2024-01-20T10:30:00",
                                  "status": 404,
                                  "error": "Not Found",
                                  "message": "Receita não encontrada com ID 1",
                                  "path": "/api/v1/recipes/1"
                                }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este recurso")
    ])
    @PreAuthorize("hasRole('ADMIN')")
    fun updateRecipe(
        @PathVariable id: Long,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = RecipeUpdateRequest::class),
                    examples = [
                        ExampleObject(
                            name = "request",
                            value = """
                                {
                                  "quantity": 0.75
                                }
                            """
                        )
                    ]
                )
            ]
        )
        @Valid @RequestBody request: RecipeUpdateRequest
    ): ResponseEntity<RecipeResponse> {
        val updatedRecipe = recipeService.update(id, request)
        return ResponseEntity.ok(updatedRecipe)
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir receita",
        description = "Remove uma receita do sistema"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Receita excluída com sucesso"),
        ApiResponse(responseCode = "404", description = "Receita não encontrada"),
        ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este recurso")
    ])
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<Void> {
        recipeService.delete(id)
        return ResponseEntity.noContent().build()
    }
} 