package com.epam.service

import com.epam.exception.CustomException
import com.epam.model.Storage
import com.epam.repository.StorageRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class StorageService(
    private val storageRepository: StorageRepository,
) {

    @Transactional
    fun createStorage(storage: Storage): Long {
        return storageRepository.save(storage).id ?: throw CustomException(
            "Error happened during storage creation",
            500
        )
    }

    @Transactional
    fun deleteSongsByIds(ids: List<Long>): List<Storage> {
        val storagesToDelete = storageRepository.findAllById(ids)
        storageRepository.deleteAllById(ids)

        return storagesToDelete
    }

    fun getStorages(): List<Storage> {
        return storageRepository.findAll()
    }
}
