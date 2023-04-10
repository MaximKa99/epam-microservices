package com.epam.service.adapter

import com.epam.api.SongApi
import com.epam.dto.DeleteSongIds
import com.epam.dto.SongIdView
import com.epam.dto.SongView
import com.epam.exception.CustomException
import com.netflix.discovery.EurekaClient
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class SongClient(
    private val eurekaClient: EurekaClient,
    private val restTemplate: RestTemplate,
): SongApi {
    override fun deleteSongsByIds(ids: MutableList<Long>?): ResponseEntity<DeleteSongIds> {
        val songInstance = eurekaClient.getApplication("SONG").instances.firstOrNull()
            ?: throw CustomException("Song instance was not found", 500)

        return restTemplate.exchange("http://${songInstance.hostName}:${songInstance.port}/api/songs?ids={ids}", HttpMethod.DELETE,
            HttpEntity.EMPTY, DeleteSongIds::class.java, ids)
    }

    override fun getSongById(songId: Long?): ResponseEntity<SongView> {
        val songInstance = eurekaClient.getApplication("SONG").instances.firstOrNull()
            ?: throw CustomException("Song instance was not found", 500)

        return restTemplate.exchange("http://${songInstance.hostName}:${songInstance.port}/api/songs/{songId}", HttpMethod.GET,
            HttpEntity.EMPTY, SongView::class.java, songId)
    }

    override fun saveSong(songView: SongView?): ResponseEntity<SongIdView> {
        val songInstance = eurekaClient.getApplication("SONG").instances.firstOrNull()
            ?: throw CustomException("Song instance was not found", 500)

        val httpEntity = HttpEntity<SongView>(songView)
        return restTemplate.exchange("http://${songInstance.hostName}:${songInstance.port}/api/songs", HttpMethod.POST,
            httpEntity, SongIdView::class.java)
    }
}