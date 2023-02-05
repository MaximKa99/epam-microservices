package com.epam.controller

import com.epam.service.ResourceService
import com.epam.view.DeletedResourcesView
import com.epam.view.ResourceIdView
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class ResourceController(
    private val resourceService: ResourceService,
) {

    @PostMapping("/resources", consumes = ["audio/mpeg"])
    fun saveResource(
        @RequestHeader(HttpHeaders.CONTENT_TYPE) value: String,
        request: HttpServletRequest,
    ): ResourceIdView {
        val id = resourceService.saveResource(request.inputStream, value)

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