package com.epam.repository

import com.epam.ResourceType
import com.epam.model.OutboxEvent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class OutboxRepositoryTest {
    @Autowired
    private lateinit var outboxEventRepository: OutboxEventRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    fun `save event`() {
        val expected = OutboxEvent().apply {
            eventType = ResourceType.Audio.type
            payload = "test"
            isProceeded = false
        }

        outboxEventRepository.save(expected)

        val actual = entityManager.find(OutboxEvent::class.java, expected.id)

        assertEquals(expected.id, actual.id)
        assertEquals(expected.eventType, actual.eventType)
        assertEquals(expected.payload, actual.payload)
        assertEquals(expected.isProceeded, actual.isProceeded)
    }

    @Test
    fun `find all not processed events`() {
        val expected1 = OutboxEvent().apply {
            eventType = ResourceType.Audio.type
            payload = "test1"
            isProceeded = false
        }

        val expected2 = OutboxEvent().apply {
            eventType = ResourceType.Audio.type
            payload = "test2"
            isProceeded = false
        }

        val expected3 = OutboxEvent().apply {
            eventType = ResourceType.Audio.type
            payload = "test3"
            isProceeded = true
        }

        entityManager.persist(expected1)
        entityManager.persist(expected2)
        entityManager.persist(expected3)

        val actual = outboxEventRepository.findByIsProceededFalse()

        assertEquals(2, actual.size)
        assertEquals(expected1.id, actual[0].id)
        assertEquals(expected1.eventType, actual[0].eventType)
        assertEquals(expected1.payload, actual[0].payload)
        assertEquals(expected1.isProceeded, actual[0].isProceeded)
        assertEquals(expected2.id, actual[1].id)
        assertEquals(expected2.eventType, actual[1].eventType)
        assertEquals(expected2.payload, actual[1].payload)
        assertEquals(expected2.isProceeded, actual[1].isProceeded)
    }
}