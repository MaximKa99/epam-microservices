package com.epam.controller

import CustomException
import com.epam.service.SongService
import com.epam.api.SongApi
import com.epam.dto.DeleteSongIds
import com.epam.dto.SongIdView
import com.epam.dto.SongView
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SongController(
    val songService: SongService,
): SongApi {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    override fun deleteSongsByIds(ids: MutableList<Long>): ResponseEntity<DeleteSongIds> {
        return ResponseEntity.ok(DeleteSongIds().apply {
            ids.addAll(ids.map { songService.deleteSong(it) }.toMutableList())
        })
    }

    override fun getSongById(songId: Long): ResponseEntity<SongView> {
        val song = songService.getSong(songId)

        return ResponseEntity.ok(SongView().apply {
            name = song.name
            album = song.album
            artist = song.artist
            year = song.year
            length = song.length
            resourceId = song.resourceId ?: throw CustomException("Smth went wrong", 500)
        })
    }

    override fun saveSong(songView: SongView): ResponseEntity<SongIdView> {
        return ResponseEntity.ok(
            SongIdView().apply {
                id = songService.saveSong(songView)
            }
        )
    }
}