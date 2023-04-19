package com.epam.controller

import com.epam.api.ResourceApi
import com.epam.dto.DeletedResourcesView
import com.epam.dto.ResourceIdView
import com.epam.exception.CustomException
import com.epam.service.ResourceService
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ResourceController(
    private val resourceService: ResourceService,
): ResourceApi {
    override fun deleteResource(ids: List<Long>): ResponseEntity<DeletedResourcesView> {
        return ResponseEntity.ok(
            DeletedResourcesView().apply {
                this.ids = ids.map { resourceService.deleteResource(it) }
            }
        )
    }

    override fun getResource(id: Long): ResponseEntity<Resource> {
        val stream = resourceService.getResource(id)

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"$id.mp3\""
        ).body(ByteArrayResource(stream.readAllBytes()))
    }

    override fun saveResource(
        contentType: String?,
        resourceType: String?,
        file: MultipartFile?
    ): ResponseEntity<ResourceIdView> {
        if (file?.inputStream == null || resourceType == null) {
            throw CustomException("Bad request", 400)
        }

        val id = resourceService.saveResource(file.inputStream, resourceType)

        return ResponseEntity.ok(ResourceIdView().apply {
            this.id = id
        })
    }
}
