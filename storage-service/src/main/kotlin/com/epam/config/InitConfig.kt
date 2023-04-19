package com.epam.config

import com.epam.model.Storage
import com.epam.model.StorageType
import com.epam.service.StorageService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
class InitConfig(
    private val storageService: StorageService,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun createDefaultStorages() {
        val stagingStorage = Storage().apply {
            storageType = StorageType.STAGING.value()
            bucket = "bucket.staging"
            path = ""
        }
        val permanentStorage = Storage().apply {
            storageType = StorageType.PERMANENT.value()
            bucket = "bucket.permanent"
            path = ""
        }

        storageService.createStorage(stagingStorage)
        storageService.createStorage(permanentStorage)
    }
}