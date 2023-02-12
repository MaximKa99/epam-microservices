package com.epam.service.adapter

import com.epam.exception.CustomException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.utils.IoUtils
import java.io.InputStream
import java.net.URI


@Component
class AdapterS3 {
    @Autowired
    private lateinit var s3Client: S3Client

    @Retryable(include = [SdkClientException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun putResource(inputStream: InputStream, id: String, bucket: String) {
        if (!bucketExists(bucket)) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build())
        }
        val byteArray = IoUtils.toByteArray(inputStream)
        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(id).build(), RequestBody.fromBytes(byteArray))
    }

    @Retryable(include = [SdkClientException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun getResource(key: String, bucket: String): InputStream {
        if (!bucketExists(bucket)) {
            throw CustomException("bucket $bucket wasnt created!", 500)
        }

        return s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build())
    }

    @Retryable(include = [SdkClientException::class], maxAttempts = 3, backoff = Backoff(delay = 5000))
    fun deleteResource(key: String, bucket: String) {
        if (!bucketExists(bucket)) {
            throw CustomException("bucket $bucket wasnt created!", 500)
        }

        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build())
    }

    private fun bucketExists(bucketName: String): Boolean {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build())
            return true
        } catch (e: S3Exception) {
            if (e.statusCode() == 404) {
                return false
            }
            throw e
        }
    }
}