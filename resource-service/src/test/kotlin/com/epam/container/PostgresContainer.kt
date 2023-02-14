package com.epam.container

import org.springframework.boot.test.context.TestComponent
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import javax.annotation.PreDestroy

@TestComponent
class PostgresContainer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private val password = "pass"
    private val user = "user"
    private val db = "postgres"

    private val postgres = PostgreSQLContainer("postgres")
            .withDatabaseName(db)
            .withPassword(password)
            .withUsername(user)

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        postgres.start()
        TestPropertyValues.of(
                "spring.datasource.url=${postgres.jdbcUrl}",
                "spring.datasource.username=${postgres.username}",
                "spring.datasource.password=${postgres.password}",
        ).applyTo(applicationContext.environment)
    }
}