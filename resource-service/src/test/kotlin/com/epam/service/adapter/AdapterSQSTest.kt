package com.epam.service.adapter

import com.epam.config.AWSConfig
import com.epam.container.LocalStackContainer
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

@SpringBootTest(
        classes = [AdapterSQS::class, AWSConfig::class]
)
@ContextConfiguration(
        initializers = [LocalStackContainer::class]
)
@DirtiesContext
class AdapterSQSTest {
    @Autowired
    private lateinit var sqsAdapter: AdapterSQS

    @Autowired
    private lateinit var sqsClient: SqsClient

    @Test
    fun `put message`() {
        val expected = "message"
        val testQueue = "test.fifo"
        sqsAdapter.putMessage(expected, testQueue)

        val actual = sqsClient.receiveMessage(
                ReceiveMessageRequest.builder()
                        .queueUrl(sqsAdapter.getQueueUrl(testQueue))
                        .build()
        ).messages()

        assertFalse(actual.isEmpty())
        assertEquals(expected, actual.first().body())
    }
}