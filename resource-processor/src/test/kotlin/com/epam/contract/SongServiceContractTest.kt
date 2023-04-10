package com.epam.contract

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import com.epam.dto.SongIdView
import com.epam.dto.SongView
import com.epam.service.adapter.ResourceClientTest
import com.epam.service.adapter.SongClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.appinfo.InstanceInfo
import com.netflix.discovery.EurekaClient
import com.netflix.discovery.shared.Application
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

@SpringBootTest
@ExtendWith(PactConsumerTestExt::class)
class SongServiceContractTest {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Pact(provider = "song_provider", consumer = "song_consumer")
    fun `save song`(builder: PactDslWithProvider): V4Pact =
            builder
                    .given("try to save song")
                    .uponReceiving("a request to save")
                    .path("/api/songs")
                    .method("POST")
                    .body(objectMapper.writeValueAsString(SongView().apply {
                        name = "test"
                        artist = "test"
                        resourceId = 1L
                    }))
                    .willRespondWith()
                    .status(200)
                    .headers(mapOf(HttpHeaders.CONTENT_TYPE to MediaType.APPLICATION_JSON_VALUE))
                    .body(objectMapper.writeValueAsString(SongIdView().apply {
                        id = 1
                    }))
                    .toPact(V4Pact::class.java)

    @Test
    @PactTestFor(pactMethod = "save song")
    fun `run save song test`(mockServer: MockServer) {
        //TODO generalize
        val eurekaClient = Mockito.mock(EurekaClient::class.java)
        val application = Mockito.mock(Application::class.java)
        val instanceInfo = Mockito.mock(InstanceInfo::class.java)
        Mockito.`when`(eurekaClient.getApplication(Mockito.eq("SONG")))
            .thenReturn(application)
        Mockito.`when`(application.instances).thenReturn(listOf(instanceInfo))
        Mockito.`when`(instanceInfo.hostName).thenReturn("localhost")
        Mockito.`when`(instanceInfo.port).thenReturn(mockServer.getPort())
        val songClient = SongClient(eurekaClient, restTemplate)
        val actual = songClient.saveSong(SongView().apply {
            name = "test"
            artist = "test"
            resourceId = 1L
        }).body

        assertEquals(1, actual?.id)
    }
}