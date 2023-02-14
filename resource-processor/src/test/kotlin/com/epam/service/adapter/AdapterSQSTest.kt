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
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@SpringBootTest(
        classes = [AdapterSQS::class, AWSConfig::class]
)
@ContextConfiguration(
        initializers = [LocalStackContainer::class]
)
class AdapterSQSTest {
    @Autowired
    private lateinit var sqsAdapter: AdapterSQS
    @Autowired
    private lateinit var sqsClient: SqsClient

    @Test
    fun `get messages`() {
        val expected = "test"
        val testQueue = "test.fifo"

        val url = sqsClient
                .createQueue(CreateQueueRequest.builder()
                        .attributes(mapOf(
                                QueueAttributeName.FIFO_QUEUE to "true",
                                QueueAttributeName.CONTENT_BASED_DEDUPLICATION to "true"
                        ))
                        .queueName(testQueue)
                        .build())
                .queueUrl()

        sqsClient.sendMessage(SendMessageRequest.builder().messageBody(expected).messageGroupId("test").queueUrl(url).build())

        val actual = sqsAdapter.getMessages(testQueue)

        assertEquals(expected, actual.first().body())
    }

    @Test
    fun `delete message`() {
        val expected = "test"
        val testQueue = "test.fifo"

        val url = sqsClient
                .createQueue(CreateQueueRequest.builder()
                        .attributes(mapOf(
                                QueueAttributeName.FIFO_QUEUE to "true",
                                QueueAttributeName.CONTENT_BASED_DEDUPLICATION to "true"
                        ))
                        .queueName(testQueue)
                        .build())
                .queueUrl()

        sqsClient.sendMessage(SendMessageRequest.builder().messageBody(expected).messageGroupId("test").queueUrl(url).build())
        sqsClient.receiveMessage(ReceiveMessageRequest.builder().queueUrl(url).build()).messages().forEach {
            sqsAdapter.deleteMessage(testQueue, it.receiptHandle())
        }

        assertTrue(sqsClient.receiveMessage(ReceiveMessageRequest.builder().queueUrl(url).build()).messages().isEmpty())
    }
}