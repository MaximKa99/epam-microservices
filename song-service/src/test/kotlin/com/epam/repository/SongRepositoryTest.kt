package com.epam.repository

import com.epam.container.PostgresContainer
import com.epam.model.Song
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
        initializers = [PostgresContainer::class]
)
@Transactional
@DirtiesContext
class SongRepositoryTest {
    @Autowired
    private lateinit var underTest: SongRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    fun `save song`() {
        val expected = Song().apply {
            name = "name"
            album = "album"
            artist = "artist"
            resourceId = 1
            length = "1000"
        }

        underTest.save(expected)

        val actual = entityManager.find(Song::class.java, expected.id)

        assertEquals(expected.id, actual.id)
        assertEquals(expected.album, actual.album)
        assertEquals(expected.artist, actual.artist)
        assertEquals(expected.length, actual.length)
        assertEquals(expected.resourceId, actual.resourceId)
        assertEquals(expected.name, actual.name)
    }

    @Test
    fun `find song by id`() {
        val expected = Song().apply {
            name = "name"
            album = "album"
            artist = "artist"
            resourceId = 1
            length = "1000"
        }

        entityManager.persist(expected)

        val actual = underTest.findById(expected.id).get()

        assertEquals(expected.id, actual.id)
        assertEquals(expected.album, actual.album)
        assertEquals(expected.artist, actual.artist)
        assertEquals(expected.length, actual.length)
        assertEquals(expected.resourceId, actual.resourceId)
        assertEquals(expected.name, actual.name)
    }

    @Test
    fun `delete song by id`() {
        val expected = Song().apply {
            name = "name"
            album = "album"
            artist = "artist"
            resourceId = 1
            length = "1000"
        }

        entityManager.persist(expected)

        underTest.deleteById(expected.id)

        assertNull(entityManager.find(Song::class.java, expected.id))
    }
}