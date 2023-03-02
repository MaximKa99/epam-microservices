package com.epam.repository

import com.epam.model.Song
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SongRepository : CrudRepository<Song, Long>