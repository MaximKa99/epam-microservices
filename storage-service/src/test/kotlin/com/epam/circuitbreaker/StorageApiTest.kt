package com.epam.circuitbreaker

import com.epam.container.PostgresContainer
import com.epam.dto.StorageIdView
import com.epam.dto.StorageView
import com.epam.dto.StorageWithIdView
import com.epam.model.Storage
import com.epam.repository.StorageRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DirtiesContext
@ContextConfiguration(
    initializers = [PostgresContainer::class]
)
class StorageApiTest {
    @Autowired
    private lateinit var restTemplate: RestTemplate
    @Autowired
    private lateinit var storageRepository: StorageRepository
    @LocalServerPort
    private lateinit var port: String

    @ParameterizedTest
    @ValueSource(ints = [1, 2])
    fun `create storage`(number: Int) {
        val expected = StorageView().apply {
            storageType = "storageType$number"
            bucket = "bucket$number"
            path = "path$number"
        }

        val entity = HttpEntity(expected)
        val storageIdView = restTemplate.exchange(
            "http://localhost:$port/api/storages",
            HttpMethod.POST,
            entity,
            StorageIdView::class.java
        )

        val actual = storageRepository.findById(storageIdView.body?.id ?: fail { "No id" }).get()
        Assertions.assertEquals(expected.storageType, actual.storageType)
        Assertions.assertEquals(expected.bucket, actual.bucket)
        Assertions.assertEquals(expected.path, actual.path)
    }

    @Test
    fun `delete storages`() {
        val toBeDeletedStorages = listOf(1, 2).map {
            Storage().apply {
                storageType = "storageType$it"
                bucket = "bucket$it"
                path = "path$it"
            }
        }
        storageRepository.saveAll(toBeDeletedStorages)

        val actual = restTemplate.exchange<List<StorageIdView>>(
            "http://localhost:$port/api/storages?ids={ids}",
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            mapOf("ids" to "${toBeDeletedStorages[0].id},${toBeDeletedStorages[1].id}")
            ).body

        Assertions.assertEquals(toBeDeletedStorages[0].id, actual[0].id)
        Assertions.assertEquals(toBeDeletedStorages[1].id, actual[1].id)
    }

    @Test
    fun `get storages`() {
        val expected = Storage().apply {
            storageType = "expected"
            bucket = "expected"
            path = "expected"
        }

        storageRepository.save(expected)

        val response = restTemplate.exchange<List<StorageWithIdView>>(
            "http://localhost:$port/api/storages",
            HttpMethod.GET
        ).body

        Assertions.assertTrue(response.map {
            it.id
        }.contains(expected.id))
    }
}
