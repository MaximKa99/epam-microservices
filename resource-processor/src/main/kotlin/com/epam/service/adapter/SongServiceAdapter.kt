package com.epam.service.adapter

import com.epam.api.SongApi
import com.epam.dto.SongIdView
import com.epam.dto.SongView
import com.epam.exception.CustomException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException

@Service
class SongServiceAdapter(
    private val mapper: ObjectMapper,
    @Qualifier("song") private val webClient: WebClient
): SongApi {



    @Retryable(include = [WebClientRequestException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    override fun saveSong(songView: SongView): ResponseEntity<SongIdView> {
        return ResponseEntity.ok(webClient.post()
            .uri("/api/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapper.writeValueAsString(songView))
            .retrieve()
            .bodyToMono(SongIdView::class.java)
            .block() ?: throw CustomException("Error while communicating with song service", 500))
    }
}