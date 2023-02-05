package com.epam.service.adapter

import com.epam.exception.CustomException
import com.epam.view.SongIdView
import com.epam.view.SongView
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import software.amazon.awssdk.core.exception.SdkClientException

@Service
class SongServiceAdapter {
    @Autowired
    private lateinit var mapper: ObjectMapper
    private val webClient = WebClient.create("http://localhost:8070")

    @Retryable(include = [WebClientRequestException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun saveSong(songView: SongView): SongIdView {
        return webClient.post()
            .uri("/api/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapper.writeValueAsString(songView))
            .retrieve()
            .bodyToMono(SongIdView::class.java)
            .block() ?: throw CustomException("Error while communicating with song service", 500)
    }
}