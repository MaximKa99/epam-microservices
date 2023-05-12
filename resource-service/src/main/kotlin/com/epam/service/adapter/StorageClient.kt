package com.epam.service.adapter

import com.epam.api.StorageApi
import com.epam.dto.StorageIdView
import com.epam.dto.StorageView
import com.epam.dto.StorageWithIdView
import com.epam.exception.CustomException
import com.epam.view.OAuthView
import com.netflix.discovery.EurekaClient
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class StorageClient(
    private val eurekaClient: EurekaClient,
    private val restTemplate: RestTemplate,
): StorageApi {
    private val AUTH_HEADER_NAME = "Authorization"
    private val AUTH_HEADER_FORMATE = "BEARER %s"
    override fun createStorage(storageView: StorageView?): ResponseEntity<StorageIdView> {
        val storageInstance = eurekaClient.getApplication("STORAGE").instances.firstOrNull()
            ?: throw CustomException("Song instance was not found", 500)

        val headers = HttpHeaders()
        headers[AUTH_HEADER_NAME] = AUTH_HEADER_FORMATE.format(retrieveAccessToken())
        val httpEntity = HttpEntity<StorageView>(
            storageView,
            headers
        )
        return restTemplate.exchange("http://${storageInstance.hostName}:${storageInstance.port}/api/storages", HttpMethod.POST,
            httpEntity)
    }

    override fun deleteStoragesByIds(ids: List<Long>): ResponseEntity<List<StorageIdView>> {
        val storageInstance = eurekaClient.getApplication("STORAGE").instances.firstOrNull()
            ?: throw CustomException("Song instance was not found", 500)

        val headers = HttpHeaders()
        headers[AUTH_HEADER_NAME] = AUTH_HEADER_FORMATE.format(retrieveAccessToken())
        val httpEntity = HttpEntity<String>(
            headers
        )
        return restTemplate.exchange(
            "http://${storageInstance.hostName}:${storageInstance.port}/api/storages?ids={ids}",
            HttpMethod.DELETE,
            httpEntity,
            ids
        )
    }

    override fun getStorageList(): ResponseEntity<List<StorageWithIdView>> {
        val storageInstance = eurekaClient.getApplication("STORAGE").instances.firstOrNull()
            ?: throw CustomException("Song instance was not found", 500)

        val headers = HttpHeaders()
        headers[AUTH_HEADER_NAME] = AUTH_HEADER_FORMATE.format(retrieveAccessToken())
        val httpEntity = HttpEntity<String>(
            headers
        )
        return restTemplate.exchange(
            "http://${storageInstance.hostName}:${storageInstance.port}/api/storages",
            HttpMethod.GET,
            httpEntity
        )
    }

    private fun retrieveAccessToken(): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val url = "http://keycloak:8080/realms/epam/protocol/openid-connect/token"

        val form = LinkedMultiValueMap<String, String>()
        form["grant_type"] = "password"
        form["client_id"] = "postman"
        form["username"] = "admin"
        form["password"] = "admin"

        val httpEntity = HttpEntity(form, headers)

        return restTemplate.postForEntity(url, httpEntity, OAuthView::class.java).body?.accessToken
            ?: throw CustomException("Couldnt obtain access token for storage service", 500)
    }
}
