package com.epam.repository

import com.epam.ResourceType
import com.epam.model.Resource
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class ResourceRepositoryTest {
    @Autowired
    private lateinit var underTest: ResourceRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    fun `save resource`() {
        val expected = Resource().apply {
            type = ResourceType.Audio.type
            uuid = UUID.randomUUID().toString()
        }

        underTest.save(expected)

        val actual = entityManager.find(Resource::class.java, expected.id)

        assertEquals(expected.id, actual.id)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.uuid, actual.uuid)
    }

    @Test
    fun `get resource by id`() {
        val expected = Resource().apply {
            type = ResourceType.Audio.type
            uuid = UUID.randomUUID().toString()
        }

        entityManager.persist(expected)

        val actual = underTest.findById(expected.id).get()

        assertEquals(expected.id, actual.id)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.uuid, actual.uuid)
    }

    @Test
    fun `delete resource by id`() {
        val expected = Resource().apply {
            type = ResourceType.Audio.type
            uuid = UUID.randomUUID().toString()
        }

        entityManager.persist(expected)
        underTest.delete(expected)

        assertNull(entityManager.find(Resource::class.java, expected.id))
    }
}