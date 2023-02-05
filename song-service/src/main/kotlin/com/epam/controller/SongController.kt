package com.epam.controller

import com.epam.exception.CustomException
import com.epam.service.SongService
import com.epam.view.DeleteSongIds
import com.epam.view.SongIdView
import com.epam.view.SongView
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SongController(
    val songService: SongService,
)
{

    @PostMapping("/songs")
    fun saveSong(@RequestBody songView: SongView): SongIdView {
        return SongIdView(songService.saveSong(songView))
    }

    @GetMapping("/songs/{id}")
    fun getSong(@PathVariable id: Int): SongView {
        val song = songService.getSong(id)

        return SongView(
            name = song.name,
            album = song.album,
            artist = song.artist,
            year = song.year,
            length = song.length,
            resourceId = song.resourceId ?: throw CustomException("Smth went wrong", 500)
        )
    }

    @DeleteMapping("/songs")
    fun deleteSongs(@RequestParam ids: List<Int>): DeleteSongIds {
        return DeleteSongIds(ids.map { songService.deleteSong(it) })
    }
}