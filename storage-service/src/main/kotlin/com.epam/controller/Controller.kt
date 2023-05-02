package com.epam.controller

import com.epam.api.StorageApi
import com.epam.dto.StorageIdView
import com.epam.dto.StorageView
import com.epam.dto.StorageWithIdView
import com.epam.model.Storage
import com.epam.service.StorageService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    private val storageService: StorageService,
): StorageApi {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)
    override fun createStorage(storageView: StorageView?): ResponseEntity<StorageIdView> {
        LOGGER.info("Received request to create storage $storageView")

        val creatingStorage = Storage().apply {
            storageType = storageView?.storageType
            bucket = storageView?.bucket
            path = storageView?.path
        }

        return ResponseEntity.ok(StorageIdView().apply {
            id = storageService.createStorage(creatingStorage)
        })
    }

    override fun deleteStoragesByIds(ids: MutableList<Long>): ResponseEntity<MutableList<StorageIdView>> {
        LOGGER.info("Received reqeust to delete storages with ids $ids")

        return ResponseEntity.ok(storageService.deleteSongsByIds(ids.toList()).map {
                StorageIdView().apply {
                    this.id = it.id
                }
            }.toMutableList())
    }

    override fun getStorageList(): ResponseEntity<MutableList<StorageWithIdView>> {
        LOGGER.info("Received request to get the list of storages")

        return ResponseEntity.ok(storageService.getStorages().map {
            StorageWithIdView().apply {
                id = it.id
                storageType = it.storageType
                bucket = it.bucket
                path = it.path
            }
        }.toMutableList())
    }
}