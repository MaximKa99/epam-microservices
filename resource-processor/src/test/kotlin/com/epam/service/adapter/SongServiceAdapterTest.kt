package com.epam.service.adapter

import com.epam.config.ExternalServicesConfig
import com.epam.dto.SongIdView
import com.epam.dto.SongView
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest(
        classes = [SongServiceAdapter::class, JacksonAutoConfiguration::class, ExternalServicesConfig::class]
)
class SongServiceAdapterTest {
    @Autowired
    private lateinit var underTest: SongServiceAdapter
    @Autowired
    private lateinit var mapper: ObjectMapper

    companion object {
        @JvmStatic
        private val wiremock = WireMockServer(WireMockConfiguration().notifier(ConsoleNotifier(true)).dynamicPort())

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("services.song", wiremock::baseUrl)
        }

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
        val expected = SongIdView(1)

        wiremock.stubFor(post("/api/songs")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(mapper.writeValueAsString(expected))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        )

        val actual = underTest.saveSong(SongView(
                resourceId = 1
        )).body

        assertEquals(expected.id, actual?.id)
    }
}