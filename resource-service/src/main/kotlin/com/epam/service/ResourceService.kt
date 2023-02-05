package com.epam.service

import com.epam.ResourceType
import com.epam.event.ResourceIdEvent
import com.epam.exception.CustomException
import com.epam.model.OutboxEvent
import com.epam.model.Resource
import com.epam.repository.OutboxEventRepository
import com.epam.repository.ResourceRepository
import com.epam.service.adapter.AdapterS3
import com.epam.service.adapter.AdapterSQS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
) {

    @Transactional
    fun saveResource(stream: InputStream, type: String): Int {
        val resource = Resource()

        when (ResourceType.of(type)) {
            ResourceType.Audio -> {
                val uuid = UUID.randomUUID().toString()
                resource.type = ResourceType.Audio.type
                resource.uuid = uuid
                val id = resourceRepository.save(resource).id ?: throw CustomException("smth went wrong", 500)

                //TODO outbox is needed here
                adapterS3.putResource(stream, uuid, ResourceType.Audio.bucket)

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
    fun getResource(id: Int): InputStream {
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
    fun deleteResource(id: Int): Int {
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

                        adapterSQS.putMessage(event.resourceId.toString(), ResourceType.Audio.queue)

                        it.isProceeded = true
                        outboxEventRepository.save(it)
                    }
                    else -> throw CustomException("Unrecognised event type", 400)
                }
            }
}