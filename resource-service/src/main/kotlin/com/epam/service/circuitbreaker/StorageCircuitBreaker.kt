package com.epam.service.circuitbreaker

import com.epam.api.StorageApi
import com.epam.dto.StorageIdView
import com.epam.dto.StorageView
import com.epam.dto.StorageWithIdView
import com.epam.exception.CustomException
import com.epam.service.adapter.StorageClient
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.vavr.control.Try
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

@Component("CircuitBreaker")
class StorageCircuitBreaker(
    private val storageClient: StorageClient,
): StorageApi {
    private val circuitBreakerConfig = CircuitBreakerConfig.custom()
        .recordExceptions(IOException::class.java, TimeoutException::class.java, UnknownHostException::class.java)
        .build()
    private val circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig)
    private val circuitBreaker = circuitBreakerRegistry.circuitBreaker("storage")

    override fun createStorage(storageView: StorageView?): ResponseEntity<StorageIdView> {
        return circuitBreaker.executeTrySupplier {
            Try.ofSupplier {
                storageClient.createStorage(storageView)
            }
        }.getOrElseThrow { exception ->
            CustomException(exception.message, 500)
        }
    }

    override fun deleteStoragesByIds(ids: List<Long>): ResponseEntity<List<StorageIdView>> {
        return circuitBreaker.executeTrySupplier {
            Try.ofSupplier {
                storageClient.deleteStoragesByIds(ids)
            }
        }.getOrElseThrow { exception ->
            CustomException(exception.message, 500)
        }
    }

    override fun getStorageList(): ResponseEntity<List<StorageWithIdView>> {
        return circuitBreaker.executeTrySupplier {
            Try.ofSupplier {
                storageClient.getStorageList()
            }
        }.getOrElseGet {
            ResponseEntity.ok(
                listOf(
                    StorageWithIdView().apply {
                        id = 0
                        storageType = "STAGING"
                        bucket = "audio.staging"
                        path = ""
                    },
                    StorageWithIdView().apply {
                        id = 1
                        storageType = "PERMANENT"
                        bucket = "audio.permanent"
                        path = ""
                    }
                )
            )
        }
    }
}
