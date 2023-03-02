package com.epam.contract

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import com.epam.dto.SongIdView
import com.epam.dto.SongView
import com.epam.service.adapter.SongServiceAdapter
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@ExtendWith(PactConsumerTestExt::class)
class SongServiceContractTest {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Pact(provider = "song_provider", consumer = "song_consumer")
    fun `save song`(builder: PactDslWithProvider): V4Pact =
            builder
                    .given("try to save song")
                    .uponReceiving("a request to save")
                    .path("/api/songs")
                    .method("POST")
                    .body(objectMapper.writeValueAsString(SongView(
                        name = "test",
                        artist = "test",
                        resourceId = 1L
                    )))
                    .willRespondWith()
                    .status(200)
                    .headers(mapOf(HttpHeaders.CONTENT_TYPE to MediaType.APPLICATION_JSON_VALUE))
                    .body(objectMapper.writeValueAsString(SongIdView(1)))
                    .toPact(V4Pact::class.java)

    @Test
    @PactTestFor(pactMethod = "save song")
    fun `run save song test`(mockServer: MockServer) {
        val songServiceAdapter = SongServiceAdapter(objectMapper, WebClient.create(mockServer.getUrl()))
        val actual = songServiceAdapter.saveSong(SongView(
            name = "test",
            artist = "test",
            resourceId = 1L
        )).body

        assertEquals(1, actual?.id)
    }
}