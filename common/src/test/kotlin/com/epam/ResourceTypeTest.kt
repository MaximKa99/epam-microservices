package com.epam

import com.epam.exception.CustomException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ResourceTypeTest {
    @Test
    fun `define unknown type`() {
        val actual = assertThrows(CustomException::class.java) { ResourceType.of("unknown") }

        assertEquals( "Unrecognised type", actual.message)
        assertEquals(400, actual.code)
    }

    @Test
    fun `define type`() {
        val actual = ResourceType.of("audio/mpeg")

        assertTrue(actual == ResourceType.Audio)
    }
}