package com.epam.service.adapter

import com.epam.api.StorageApi
import com.epam.dto.StorageIdView
import com.epam.dto.StorageView
import com.epam.dto.StorageWithIdView
import com.epam.exception.CustomException
import com.netflix.discovery.EurekaClient
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class StorageClient(
    private val eurekaClient: EurekaClient,
    private val restTemplate: RestTemplate,
): StorageApi {
    override fun createStorage(storageView: StorageView?): ResponseEntity<StorageIdView> {
        val storageInstance = eurekaClient.getApplication("STORAGE").instances.firstOrNull()
            ?: throw CustomException("Song instance was not found", 500)

        val httpEntity = HttpEntity<StorageView>(storageView)
        return restTemplate.exchange("http://${storageInstance.hostName}:${storageInstance.port}/api/storages", HttpMethod.POST,
            httpEntity)
    }

    override fun deleteStoragesByIds(ids: List<Long>): ResponseEntity<List<StorageIdView>> {
        val storageInstance = eurekaClient.getApplication("STORAGE").instances.firstOrNull()
            ?: throw CustomException("Song instance was not found", 500)

        return restTemplate.exchange("http://${storageInstance.hostName}:${storageInstance.port}/api/storages?ids={ids}", HttpMethod.DELETE,
            HttpEntity.EMPTY, ids)
    }

    override fun getStorageList(): ResponseEntity<List<StorageWithIdView>> {
        val storageInstance = eurekaClient.getApplication("STORAGE").instances.firstOrNull()
            ?: throw CustomException("Song instance was not found", 500)

        return restTemplate.exchange("http://${storageInstance.hostName}:${storageInstance.port}/api/storages", HttpMethod.GET,
            HttpEntity.EMPTY)
    }
}