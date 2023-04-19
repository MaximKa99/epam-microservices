package com.epam.service

import com.epam.api.StorageApi
import com.epam.model.event.ResourceIdEvent
import com.epam.exception.CustomException
import com.epam.model.OutboxEvent
import com.epam.model.Resource
import com.epam.model.ResourceType
import com.epam.repository.OutboxEventRepository
import com.epam.repository.ResourceRepository
import com.epam.service.adapter.AdapterS3
import com.epam.service.adapter.AdapterSQS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.util.UUID

@Service
class ResourceService(
    private val resourceRepository: ResourceRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val adapterS3: AdapterS3,
    private val adapterSQS: AdapterSQS,
    private val mapper: ObjectMapper,
    @Qualifier("CircuitBreaker") private val storageApi: StorageApi,
) {
    private var stagingBucket = ""

    @Transactional
    fun saveResource(stream: InputStream, type: String): Long {
        val resource = Resource()

        when (ResourceType.of(type)) {
            ResourceType.Audio -> {
                val uuid = UUID.randomUUID().toString()
                resource.type = ResourceType.Audio.type
                resource.uuid = uuid
                val id = resourceRepository.save(resource).id ?: throw CustomException("smth went wrong", 500)

                val storages = storageApi.storageList.body
                val stagingStorage = storages?.firstOrNull {
                    it.storageType == "STAGING"
                } ?: throw CustomException("No staging storage was found", 500)

                //TODO outbox is needed here
                stagingBucket = stagingStorage.bucket
                adapterS3.putResource(stream, uuid, stagingBucket)

                val event = ResourceIdEvent(id)
                
                val outboxEvent = OutboxEvent()
                outboxEvent.eventType = ResourceIdEvent.EVENT_TYPE
                outboxEvent.payload = mapper.writeValueAsString(event)
                outboxEvent.isProceeded = false

                outboxEventRepository.save(outboxEvent)

                return id
            }
        }
    }

    @Transactional(readOnly = true)
    fun getResource(id: Long): InputStream {
        val resourceOptional = resourceRepository.findById(id)

        if (resourceOptional.isEmpty) {
            throw CustomException("No resource with id $id", 404)
        }

        val resource = resourceOptional.get()
        val type = resource.type ?: throw CustomException("Smth went wrong", 500)
        val key = resource.uuid ?: throw CustomException("Smth went wrong", 500)


        return when(ResourceType.of(type)) {
            ResourceType.Audio -> adapterS3.getResource(key, ResourceType.Audio.bucket)
        }
    }

    @Transactional
    fun deleteResource(id: Long): Long {
        val resourceOptional = resourceRepository.findById(id)

        if (resourceOptional.isEmpty) {
            throw CustomException("No resource with id $id", 404)
        }

        val resource = resourceOptional.get()
        val type = resource.type ?: throw CustomException("Smth went wrong", 500)
        val key = resource.uuid ?: throw CustomException("Smth went wrong", 500)

        when(ResourceType.of(type)) {
            ResourceType.Audio -> adapterS3.deleteResource(key, ResourceType.Audio.bucket)
        }

        resourceRepository.deleteById(id)

        return id
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    fun processOutbox() =
        outboxEventRepository.findByIsProceededFalse()
            .forEach {
                when (it.eventType) {
                    ResourceIdEvent.EVENT_TYPE -> {
                        val event: ResourceIdEvent = mapper.readValue(it.payload ?: throw CustomException("Wrong outbox event. No payload!", 400))

                        adapterSQS.putMessage(event.resourceId.toString(), ResourceType.Audio.queueOut)

                        it.isProceeded = true
                        outboxEventRepository.save(it)
                    }
                    else -> throw CustomException("Unrecognised event type", 400)
                }
            }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    fun updateResourceStatus() {
        val storages = storageApi.storageList.body
        val permanentStorage = storages?.firstOrNull {
            it.storageType == "PERMANENT"
        } ?: throw CustomException("No staging storage was found", 500)

        val ids = adapterSQS.getMessages(ResourceType.Audio.queueIn)

        val entitiesToBeUpdated = resourceRepository.findAllById(ids)
        entitiesToBeUpdated.forEach {
            it.bucket = permanentStorage.bucket
        }

        ids.forEach {
            adapterS3.copyResource(it.toString(), stagingBucket, permanentStorage.bucket)
        }
    }
}