package com.epam.service.adapter

import com.epam.config.WiremockConfig
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException

@SpringBootTest(
        classes = [
            WiremockConfig::class
        ]
)
class ResourceServiceAdapterTest {
    @Autowired
    private lateinit var wiremock: WireMockServer

    @Autowired
    private lateinit var underTest: ResourceServiceAdapter

    @Test
    fun `get resource`() {
        val expected = byteArrayOf(1, 2, 3, 4, 5)

        wiremock.stubFor(
                get("/api/resources/1")
                        .willReturn(aResponse().withBody(expected))
        )

        val actual = underTest.getResource("1")

        assertArrayEquals(expected, actual)
    }

    @Test
    fun `get non-existing resource`() {
        val expected = "No resource with id 2"

        wiremock.stubFor(
                get("/api/resources/2")
                        .willReturn(aResponse().withStatus(404).withBody(expected))
        )

        val actual = assertThrows(HttpClientErrorException::class.java){ underTest.getResource("2") }

        assertEquals(expected, actual.responseBodyAsString)
        assertEquals(HttpStatus.NOT_FOUND, actual.statusCode)
    }
}