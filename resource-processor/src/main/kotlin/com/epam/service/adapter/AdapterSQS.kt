package com.epam.service.adapter

import com.epam.exception.CustomException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.net.URI

@Service
class AdapterSQS {

    private val sqsClient: SqsClient = SqsClient.builder()
        .region(Region.US_EAST_1)
        .endpointOverride(URI.create("http://localhost:4566"))
        .build()

    @Retryable(include = [SdkClientException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun getMessages(queueName: String): List<Message> {
        val url = getQueueUrl(queueName)

        return when (url) {
            null -> return emptyList()
            else -> sqsClient
                .receiveMessage(ReceiveMessageRequest.builder().queueUrl(url).build())
                .messages()
        }
    }

    @Retryable(include = [SdkClientException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun deleteMessage(queueName: String, receiptHandler: String) {
        val url = getQueueUrl(queueName)

        when (url) {
            null -> throw CustomException("No such queue: $queueName", 500)
            else -> sqsClient
                .deleteMessage(DeleteMessageRequest.builder().queueUrl(url).receiptHandle(receiptHandler).build())
        }
    }

    private fun getQueueUrl(queueName: String): String? {
        return sqsClient.listQueues(ListQueuesRequest.builder().queueNamePrefix(queueName).build())
            .queueUrls()
            .firstOrNull()
    }
}