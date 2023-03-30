package com.epam.service.adapter

import com.epam.api.ResourceApi
import com.epam.dto.DeletedResourcesView
import com.epam.dto.ResourceIdView
import com.epam.exception.CustomException
import com.netflix.discovery.EurekaClient
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

@Component
class ResourceClient(
    private val eurekaClient: EurekaClient,
    private val restTemplate: RestTemplate,
): ResourceApi {
    override fun deleteResource(ids: MutableList<Long>?): ResponseEntity<DeletedResourcesView> {
        val resourceInstance = eurekaClient.getApplication("RESOURCE").instances.firstOrNull()
            ?: throw CustomException("Resource instance was not found", 500)

        return restTemplate.exchange("http://${resourceInstance.hostName}:${resourceInstance.port}/api/resources?ids={ids}", HttpMethod.DELETE,
            HttpEntity.EMPTY, DeletedResourcesView::class.java, ids)
    }

    override fun getResource(id: Long?): ResponseEntity<Resource> {
        val resourceInstance = eurekaClient.getApplication("RESOURCE").instances.firstOrNull()
            ?: throw CustomException("Resource instance was not found", 500)

        return restTemplate.exchange("http://${resourceInstance.hostName}:${resourceInstance.port}/api/resources/{id}", HttpMethod.GET,
            HttpEntity.EMPTY, Resource::class.java, id)
    }

    override fun saveResource(
        contentType: String?,
        resourceType: String?,
        file: MultipartFile?
    ): ResponseEntity<ResourceIdView> {
        val resourceInstance = eurekaClient.getApplication("RESOURCE").instances.firstOrNull()
            ?: throw CustomException("Resource instance was not found", 500)

        val headers = LinkedMultiValueMap<String, String>()
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
        val body = LinkedMultiValueMap<String, Any>()
        body.add("file", file)
        val httpEntity = HttpEntity<Map<String, Any>>(body, headers)
        return restTemplate.exchange("http://${resourceInstance.hostName}:${resourceInstance.port}/api/songs", HttpMethod.POST,
            httpEntity, ResourceIdView::class.java)
    }
}