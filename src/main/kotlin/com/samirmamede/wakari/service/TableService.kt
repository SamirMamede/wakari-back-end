package com.samirmamede.wakari.service

import com.samirmamede.wakari.exception.ResourceAlreadyExistsException
import com.samirmamede.wakari.exception.ResourceNotFoundException
import com.samirmamede.wakari.model.RestaurantTable
import com.samirmamede.wakari.model.TableStatus
import com.samirmamede.wakari.repository.TableRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TableService(private val tableRepository: TableRepository) {
    
    fun findAll(pageable: Pageable): Page<RestaurantTable> = 
        tableRepository.findAll(pageable)
    
    fun findById(id: Long): RestaurantTable = 
        tableRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Mesa não encontrada com ID: $id") }
    
    fun findByNumber(number: Int): RestaurantTable? = 
        tableRepository.findByNumber(number).orElse(null)
    
    fun findByStatus(status: TableStatus): List<RestaurantTable> = 
        tableRepository.findByStatus(status)
    
    @Transactional
    fun create(table: RestaurantTable): RestaurantTable {
        // Verificar se já existe mesa com o mesmo número
        if (tableRepository.findByNumber(table.number).isPresent) {
            throw ResourceAlreadyExistsException("Já existe uma mesa com o número ${table.number}")
        }
        
        return tableRepository.save(table)
    }
    
    @Transactional
    fun update(id: Long, updatedTable: RestaurantTable): RestaurantTable {
        val existingTable = findById(id)
        
        // Se estiver alterando o número da mesa, verificar se já existe outra mesa com esse número
        if (existingTable.number != updatedTable.number &&
            tableRepository.findByNumber(updatedTable.number).isPresent) {
            throw ResourceAlreadyExistsException("Já existe uma mesa com o número ${updatedTable.number}")
        }
        
        // Atualizar os campos da mesa existente mas manter o pedido atual
        val tableToUpdate = existingTable.copy(
            number = updatedTable.number,
            capacity = updatedTable.capacity,
            status = updatedTable.status,
            currentOrder = existingTable.currentOrder, // Manter o pedido atual
            updatedAt = LocalDateTime.now()
        )
        
        return tableRepository.save(tableToUpdate)
    }
    
    @Transactional
    fun updateStatus(id: Long, status: TableStatus): RestaurantTable {
        val table = findById(id)
        
        // Validar transições de estado específicas
        // Por exemplo, uma mesa RESERVED só pode ficar OCCUPIED ou AVAILABLE
        when (table.status) {
            TableStatus.RESERVED -> {
                if (status != TableStatus.OCCUPIED && status != TableStatus.AVAILABLE) {
                    throw IllegalStateException("Uma mesa reservada só pode ser ocupada ou disponibilizada")
                }
            }
            TableStatus.OCCUPIED -> {
                if (status == TableStatus.RESERVED) {
                    throw IllegalStateException("Uma mesa ocupada não pode ser reservada")
                }
            }
            else -> {} // Outras transições são permitidas
        }
        
        val tableToUpdate = table.copy(
            status = status,
            currentOrder = table.currentOrder, // Manter o pedido atual
            updatedAt = LocalDateTime.now()
        )
        
        return tableRepository.save(tableToUpdate)
    }
    
    @Transactional
    fun delete(id: Long) {
        val table = findById(id)
        
        // Verificar se a mesa está em uso (com pedido associado)
        if (table.currentOrder != null) {
            throw IllegalStateException("Não é possível excluir uma mesa que possui um pedido associado")
        }
        
        tableRepository.deleteById(id)
    }
    
    @Transactional
    fun cleanupTables() {
        // Buscar mesas que estão em limpeza
        val tablesInCleaning = findByStatus(TableStatus.CLEANING)
        
        // Definir todas como disponíveis novamente
        tablesInCleaning.forEach { table ->
            val updatedTable = table.copy(
                status = TableStatus.AVAILABLE,
                currentOrder = table.currentOrder, // Manter o pedido atual, embora provavelmente seja null
                updatedAt = LocalDateTime.now()
            )
            tableRepository.save(updatedTable)
        }
    }
} 