package com.samirmamede.wakari.repository

import com.samirmamede.wakari.model.RestaurantTable
import com.samirmamede.wakari.model.TableStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TableRepository : JpaRepository<RestaurantTable, Long> {
    fun findByNumber(number: Int): Optional<RestaurantTable>
    fun findByStatus(status: TableStatus): List<RestaurantTable>
} 