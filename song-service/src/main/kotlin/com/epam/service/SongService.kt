package com.epam.service

import CustomException
import com.epam.dto.SongView
import com.epam.model.Song
import com.epam.repository.SongRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class SongService(
    private val songRepository: SongRepository,
) {

    @Transactional
    fun saveSong(songView: SongView): Long {
        val song = Song()

        with(song) {
            name = songView.name
            album = songView.album
            artist = songView.artist
            resourceId = songView.resourceId
            year = songView.year
            length = songView.length
        }

        return songRepository.save(song).id ?: throw CustomException("smth went wrong", 500)
    }

    fun getSong(id: Long): Song =
        songRepository.findById(id).getOrNull() ?: throw CustomException("song with id $id wasnt found", 404)

    @Transactional
    fun deleteSong(id: Long): Long {
        songRepository.deleteById(id)

        return id
    }
}