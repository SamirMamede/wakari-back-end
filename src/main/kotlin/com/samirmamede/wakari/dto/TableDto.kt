package com.samirmamede.wakari.dto

import com.samirmamede.wakari.model.RestaurantTable
import com.samirmamede.wakari.model.TableStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class TableRequest(
    @field:NotNull(message = "Número da mesa é obrigatório")
    @field:Positive(message = "Número da mesa deve ser maior que zero")
    val number: Int,
    
    @field:NotNull(message = "Capacidade da mesa é obrigatória")
    @field:Min(value = 1, message = "Capacidade da mesa deve ser de pelo menos 1 pessoa")
    val capacity: Int
)

data class TableUpdateRequest(
    @field:NotNull(message = "Número da mesa é obrigatório")
    @field:Positive(message = "Número da mesa deve ser maior que zero")
    val number: Int,
    
    @field:NotNull(message = "Capacidade da mesa é obrigatória")
    @field:Min(value = 1, message = "Capacidade da mesa deve ser de pelo menos 1 pessoa")
    val capacity: Int,
    
    @field:NotNull(message = "Status da mesa é obrigatório")
    val status: TableStatus
)

data class TableStatusUpdateRequest(
    @field:NotNull(message = "Status da mesa é obrigatório")
    val status: TableStatus
)

data class TableResponse(
    val id: Long,
    val number: Int,
    val capacity: Int,
    val status: TableStatus,
    val currentOrderId: Long?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(table: RestaurantTable): TableResponse {
            return TableResponse(
                id = table.id,
                number = table.number,
                capacity = table.capacity,
                status = table.status,
                currentOrderId = table.currentOrder?.id,
                createdAt = table.createdAt,
                updatedAt = table.updatedAt
            )
        }
    }
} 