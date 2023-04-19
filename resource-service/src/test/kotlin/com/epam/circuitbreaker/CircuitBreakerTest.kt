package com.epam.circuitbreaker

import com.epam.config.HttpConfig
import com.epam.service.adapter.StorageClient
import com.epam.service.circuitbreaker.StorageCircuitBreaker
import com.netflix.appinfo.InstanceInfo
import com.netflix.discovery.EurekaClient
import com.netflix.discovery.shared.Application
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = [
        StorageCircuitBreaker::class,
        StorageClient::class,
        HttpConfig::class,
    ]
)
class CircuitBreakerTest {
    @Autowired
    private lateinit var storageCircuitBreaker: StorageCircuitBreaker
    @MockBean
    private lateinit var eurekaClient: EurekaClient

    @Test
    fun `mock storage is down`() {
        val instanceInfoMock = Mockito.mock(InstanceInfo::class.java)
        Mockito.`when`(instanceInfoMock.port).thenReturn(9999)
        Mockito.`when`(instanceInfoMock.hostName).thenReturn("mock")

        val applicationMock = Mockito.mock(Application::class.java)
        Mockito.`when`(applicationMock.instances).thenReturn(listOf(instanceInfoMock))

        Mockito.`when`(eurekaClient.getApplication(eq("STORAGE"))).thenReturn(applicationMock)

        val actual  = storageCircuitBreaker.storageList

        Assertions.assertEquals(2, actual.body.size)
        Assertions.assertEquals("STAGING", actual.body[0].storageType)
        Assertions.assertEquals("PERMANENT", actual.body[1].storageType)
    }
}
