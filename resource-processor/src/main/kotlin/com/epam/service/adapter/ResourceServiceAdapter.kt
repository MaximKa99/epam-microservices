package com.epam.service.adapter

import org.springframework.http.HttpMethod
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class ResourceServiceAdapter(
) {
    private val restTemplate = RestTemplate()

    @Retryable(include = [RestClientException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun getResource(resourceId: String): ByteArray? {
        return restTemplate.execute(URI.create("http://localhost:8080/api/resources/$resourceId"), HttpMethod.GET, null) {
                it.body.readAllBytes()
        }
    }
}