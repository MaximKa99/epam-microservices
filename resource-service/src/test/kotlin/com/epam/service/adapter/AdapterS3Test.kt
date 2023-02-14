package com.epam.service.adapter

import com.epam.config.AWSConfig
import com.epam.container.LocalStackContainer
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
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.util.UUID

@SpringBootTest(
        classes = [AdapterS3::class, AWSConfig::class]
)
@ContextConfiguration(
        initializers = [LocalStackContainer::class]
)
@DirtiesContext
class AdapterS3Test {
    @Autowired
    private lateinit var s3Adapter: AdapterS3

    @Autowired
    private lateinit var s3Client: S3Client
    @Test
    fun `put resource`() {
        val expected = byteArrayOf(1, 2, 3, 4, 5)
        val bucket = "test"
        val key = UUID.randomUUID().toString()

        s3Adapter.putResource(ByteArrayInputStream(expected), key, bucket)

        val actual = s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build()).readAllBytes()

        assertArrayEquals(expected, actual)
    }

    @Test
    fun `get resource`() {
        val expected = byteArrayOf(1, 2, 3, 4, 5)
        val bucket = "test"
        val key = UUID.randomUUID().toString()

        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build())
        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(), RequestBody.fromBytes(expected))

        val actual = s3Adapter.getResource(key, bucket).readAllBytes()

        assertArrayEquals(expected, actual)
    }

    @Test
    fun `delete resource`() {
        val expected = byteArrayOf(1, 2, 3, 4, 5)
        val bucket = "test"
        val key = UUID.randomUUID().toString()

        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build())
        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(), RequestBody.fromBytes(expected))

        s3Adapter.deleteResource(key, bucket)

        assertThrows(NoSuchKeyException::class.java) { s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build()) }
    }
}