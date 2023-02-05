package com.epam.repository

import com.epam.model.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OutboxEventRepository : JpaRepository<OutboxEvent, Int> {
    fun findByIsProceededFalse(): List<OutboxEvent>
}