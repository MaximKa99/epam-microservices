package com.epam.service.adapter

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.netflix.appinfo.InstanceInfo
import com.netflix.discovery.EurekaClient
import com.netflix.discovery.shared.Application
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@SpringBootTest
class ResourceClientTest {
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
    fun `get resource`() {
        val eurekaClient = Mockito.mock(EurekaClient::class.java)
        val application = Mockito.mock(Application::class.java)
        val instanceInfo = Mockito.mock(InstanceInfo::class.java)
        Mockito.`when`(eurekaClient.getApplication(Mockito.eq("RESOURCE")))
            .thenReturn(application)
        Mockito.`when`(application.instances).thenReturn(listOf(instanceInfo))
        Mockito.`when`(instanceInfo.hostName).thenReturn("localhost")
        Mockito.`when`(instanceInfo.port).thenReturn(wiremock.port())
        val underTest = ResourceClient(eurekaClient, restTemplate)
        val expected = byteArrayOf(1, 2, 3, 4, 5)

        wiremock.stubFor(
                get("/api/resources/1")
                        .willReturn(aResponse().withBody(expected))
        )

        val actual = underTest.getResource(1).body.inputStream.readAllBytes()

        assertArrayEquals(expected, actual)
    }

    @Test
    fun `get non-existing resource`() {
        val eurekaClient = Mockito.mock(EurekaClient::class.java)
        val application = Mockito.mock(Application::class.java)
        val instanceInfo = Mockito.mock(InstanceInfo::class.java)
        Mockito.`when`(eurekaClient.getApplication(Mockito.eq("RESOURCE")))
            .thenReturn(application)
        Mockito.`when`(application.instances).thenReturn(listOf(instanceInfo))
        Mockito.`when`(instanceInfo.hostName).thenReturn("localhost")
        Mockito.`when`(instanceInfo.port).thenReturn(wiremock.port())
        val underTest = ResourceClient(eurekaClient, restTemplate)
        val expected = "No resource with id 2"

        wiremock.stubFor(
                get("/api/resources/2")
                        .willReturn(aResponse().withStatus(404).withBody(expected))
        )

        assertThrows(HttpClientErrorException::class.java) {
            underTest.getResource(2)
        }
    }
}