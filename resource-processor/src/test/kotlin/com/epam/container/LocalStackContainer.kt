package com.epam.container

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class LocalStackContainer: ApplicationContextInitializer<ConfigurableApplicationContext> {
    private val localStack = GenericContainer(DockerImageName.parse("localstack/localstack"))
            .withExposedPorts(4566)

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        localStack.start()
        TestPropertyValues.of(
                "aws.endpoint=http://localhost:${localStack.firstMappedPort}",
        ).applyTo(applicationContext.environment)
    }
}