package com.epam.service

import com.epam.exception.CustomException
import com.epam.model.Song
import com.epam.repository.SongRepository
import com.epam.view.SongView
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class SongServiceTest {
    private val repository = mockk<SongRepository>()
    private val underTest = SongService(repository)
    @AfterEach
    fun clean() {
        clearAllMocks()
    }

    @Test
    fun `save song`() {
        val expected = 1

        every { repository.save(any()) } returns Song().apply {
            id = expected
        }

        val songView = SongView(resourceId = 1)
        val actualResult = underTest.saveSong(songView)

        Assertions.assertEquals(expected, actualResult)
    }

    @Test
    fun `save song exception is thrown`() {
        every { repository.save(any()) } returns Song()

        val expected = CustomException("smth went wrong", 500)

        val songView = SongView(resourceId = 1)
        val actual = Assertions.assertThrows(CustomException::class.java) { underTest.saveSong(songView) }

        Assertions.assertEquals(expected.code, actual.code)
        Assertions.assertEquals(expected.message, actual.message)
    }

    @Test
    fun `get song by id`() {
        every { repository.findById(any()) } returns Optional.of(Song().apply {
            id = 1
            resourceId = 1
        })

        val actual = underTest.getSong(1)

        Assertions.assertEquals(1, actual.id)
        Assertions.assertEquals(1, actual.resourceId)
    }

    @Test
    fun `get song not found`() {
        every { repository.findById(any()) } returns Optional.empty()

        val actual = Assertions.assertThrows(CustomException::class.java) { underTest.getSong(1) }

        Assertions.assertEquals("song with id 1 wasnt found", actual.message)
        Assertions.assertEquals(404, actual.code)
    }
}