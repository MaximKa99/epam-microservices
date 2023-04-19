package com.epam.service

import com.epam.api.StorageApi
import com.epam.dto.StorageView
import com.epam.dto.StorageWithIdView
import com.epam.exception.CustomException
import com.epam.model.OutboxEvent
import com.epam.model.Resource
import com.epam.model.ResourceType
import com.epam.repository.OutboxEventRepository
import com.epam.repository.ResourceRepository
import com.epam.service.adapter.AdapterS3
import com.epam.service.adapter.AdapterSQS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.io.InputStream
import java.util.*

class ResourceServiceTest {
    private val resourceRepository = mockk<ResourceRepository>()
    private val outBoxEventRepository = mockk<OutboxEventRepository>()
    private val adapterS3 = mockk<AdapterS3>()
    private val adapterSQS = mockk<AdapterSQS>()
    private val mapper = ObjectMapper().registerKotlinModule()
    private val storageApi = mockk<StorageApi>()

    private val underTest = ResourceService(
            resourceRepository,
            outBoxEventRepository,
            adapterS3,
            adapterSQS,
            mapper,
            storageApi,
    )

    @AfterEach
    fun clean() {
        clearAllMocks()
    }

    @Test
    fun `save resource`() {
        val expected = 1L
        every { resourceRepository.save(any()) } returns Resource().apply {
            id = expected
        }
        justRun { adapterS3.putResource(any(), any(), any()) }
        every { outBoxEventRepository.save(any()) } returns OutboxEvent()

        every { storageApi.storageList } returns ResponseEntity.ok(listOf(StorageWithIdView().apply {
            id = 1
            storageType = "STAGING"
            bucket = "staging.bucket"
        }).toMutableList())

        val actual = underTest.saveResource(InputStream.nullInputStream(), ResourceType.Audio.type)

        assertEquals(expected, actual)
    }

    @Test
    fun `get resource by id`() {
        every { resourceRepository.findById(any()) } returns Optional.of(Resource().apply {
            id = 1
            type = ResourceType.Audio.type
            uuid = UUID.randomUUID().toString()
        })
        every { adapterS3.getResource(any(), any()) } returns InputStream.nullInputStream()

        assertDoesNotThrow { underTest.getResource(1) }
    }

    @Test
    fun `get resource by id not found`() {
        every { resourceRepository.findById(any()) } returns Optional.empty()

        val actual = assertThrows(CustomException::class.java) { underTest.getResource(1) }

        assertEquals("No resource with id 1", actual.message)
        assertEquals(404, actual.code)
    }

    @Test
    fun `delete resource by id not found`() {
        every { resourceRepository.findById(any()) } returns Optional.empty()

        val actual = assertThrows(CustomException::class.java) { underTest.deleteResource(1) }

        assertEquals("No resource with id 1", actual.message)
        assertEquals(404, actual.code)
    }
}