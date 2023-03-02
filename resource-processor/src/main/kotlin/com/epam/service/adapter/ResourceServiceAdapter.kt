package com.epam.service.adapter

import com.epam.api.ResourceApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class ResourceServiceAdapter(
    @Value("\${services.resource}") private val host: String
): ResourceApi {
    private val restTemplate = RestTemplate()

    @Retryable(include = [RestClientException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    override fun getResource(id: Long): ResponseEntity<Resource> {
        return ResponseEntity.ok(restTemplate.execute(URI.create("$host/api/resources/$id"), HttpMethod.GET, null) {
            ByteArrayResource(it.body.readAllBytes())
        })
    }
}