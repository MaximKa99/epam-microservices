package com.epam.service.adapter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.net.URI

@Service
class AdapterSQS {
    @Autowired
    private lateinit var sqsClient: SqsClient

    @Retryable(include = [SdkClientException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun putMessage(message: String, queueName: String) {
        val url = getQueueUrl(queueName) ?: sqsClient
            .createQueue(CreateQueueRequest.builder()
                .attributes(mapOf(
                    QueueAttributeName.FIFO_QUEUE to "true",
                    QueueAttributeName.CONTENT_BASED_DEDUPLICATION to "true"
                ))
                .queueName(queueName)
                .build())
            .queueUrl()

        sqsClient.sendMessage(SendMessageRequest.builder()
            .messageBody(message)
            .queueUrl(url)
            .messageGroupId("resource-service")
            .build())
    }

    fun getMessages(queueName: String): List<Long> {
        val url = getQueueUrl(queueName) ?: sqsClient
            .createQueue(CreateQueueRequest.builder()
                .attributes(mapOf(
                    QueueAttributeName.FIFO_QUEUE to "true",
                    QueueAttributeName.CONTENT_BASED_DEDUPLICATION to "true"
                ))
                .queueName(queueName)
                .build())
            .queueUrl()

        return sqsClient.receiveMessage(ReceiveMessageRequest.builder()
            .queueUrl(url)
            .build()).messages().map {
                it.body().toLong()
        }
    }

    fun getQueueUrl(queueName: String): String? {
        return sqsClient
            .listQueues(ListQueuesRequest.builder()
                .queueNamePrefix(queueName)
                .build())
            .queueUrls()
            .firstOrNull()
    }
}