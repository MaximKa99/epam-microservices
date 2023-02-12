package com.epam.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class AWSConfig {
    @Value("\${aws.endpoint}")
    private lateinit var awsEndpoint: String

    private val awsCredentials = object : AwsCredentials {
        override fun accessKeyId() = "test"

        override fun secretAccessKey() = "test"
    }

    @Bean
    fun sqsClient(): SqsClient = SqsClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create(awsEndpoint))
            .credentialsProvider { awsCredentials }
            .build()

    @Bean
    fun s3Client(): S3Client = S3Client.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create(awsEndpoint))
            .forcePathStyle(true)
            .credentialsProvider { awsCredentials }
            .build()
}