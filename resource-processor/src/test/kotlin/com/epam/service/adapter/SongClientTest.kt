package com.epam.service.adapter

import com.epam.dto.SongIdView
import com.epam.dto.SongView
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.netflix.appinfo.InstanceInfo
import com.netflix.discovery.EurekaClient
import com.netflix.discovery.shared.Application
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestTemplate

@SpringBootTest
class SongClientTest {
    private lateinit var underTest: SongClient
    @Autowired
    private lateinit var mapper: ObjectMapper
    @Autowired
    private lateinit var restTemplate: RestTemplate

    companion object {
        @JvmStatic
        private val wiremock = WireMockServer(WireMockConfiguration().notifier(ConsoleNotifier(true)).dynamicPort())

        @JvmStatic
        @BeforeAll
        fun setup() {
            wiremock.start()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            wiremock.stop()
        }
    }

    @Test
    fun `save song`() {
        val eurekaClient = Mockito.mock(EurekaClient::class.java)
        val application = Mockito.mock(Application::class.java)
        val instanceInfo = Mockito.mock(InstanceInfo::class.java)
        Mockito.`when`(eurekaClient.getApplication(Mockito.eq("SONG")))
            .thenReturn(application)
        Mockito.`when`(application.instances).thenReturn(listOf(instanceInfo))
        Mockito.`when`(instanceInfo.hostName).thenReturn("localhost")
        Mockito.`when`(instanceInfo.port).thenReturn(wiremock.port())

        val expected = SongIdView().apply {
            id = 1
        }

        underTest = SongClient(eurekaClient, restTemplate)

        wiremock.stubFor(post("/api/songs")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mapper.writeValueAsString(expected))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        )

        val actual = underTest.saveSong(SongView().apply {
            resourceId = 1
        }).body

        assertEquals(expected.id, actual?.id)
    }
}