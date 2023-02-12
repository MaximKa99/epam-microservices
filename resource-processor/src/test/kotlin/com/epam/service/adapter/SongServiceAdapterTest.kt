package com.epam.service.adapter

import com.epam.config.WiremockConfig
import com.epam.view.SongIdView
import com.epam.view.SongView
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@SpringBootTest(
        classes = [
            WiremockConfig::class
        ]
)
class SongServiceAdapterTest {
    @Autowired
    private lateinit var wiremock: WireMockServer
    @Autowired
    private lateinit var underTest: SongServiceAdapter
    @Autowired
    private lateinit var mapper: ObjectMapper

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
        ))

        assertEquals(expected.id, actual.id)
    }
}