package com.epam.service.adapter

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

@SpringBootTest
@Testcontainers
class AdapterSQSTest {
    @Container
    private val sqs = GenericContainer(DockerImageName.parse("localstack/localstack"))
            .withExposedPorts(4566)
            .withCreateContainerCmdModifier { it.withPortBindings(PortBinding(Ports.Binding.bindPort(4566), ExposedPort(4566))) }

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