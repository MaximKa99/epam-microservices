package com.epam.controller

import com.epam.ResourceType
import com.epam.service.ResourceService
import com.epam.view.DeletedResourcesView
import com.epam.view.ResourceIdView
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class ResourceController(
    private val resourceService: ResourceService,
) {

    @PostMapping("/resources", consumes = [
        MediaType.MULTIPART_FORM_DATA_VALUE
    ])
    fun saveResource(
        @RequestHeader(ResourceType.HEADER_NAME) type: String,
        @RequestPart("file") file: MultipartFile
    ): ResourceIdView {
        val id = resourceService.saveResource(file.inputStream, type)

        return ResourceIdView(id)
    }

    @GetMapping("/resources/{id}")
    fun getResource(
        @PathVariable id: Int,
        @RequestHeader(HttpHeaders.CONTENT_TYPE) value: String?,
    ): ResponseEntity<ByteArray> {
        val stream = resourceService.getResource(id)

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"$id.mp3\""
        ).body(stream.readAllBytes());
    }

    @DeleteMapping("/resources")
    fun deleteResource(
        @RequestParam ids: List<Int>
    ): DeletedResourcesView {
        return DeletedResourcesView(ids.map { resourceService.deleteResource(it) })
    }
}