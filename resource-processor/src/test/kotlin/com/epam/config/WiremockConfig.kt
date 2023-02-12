package com.epam.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import javax.annotation.PreDestroy

@TestConfiguration
class WiremockConfig {
    private val wiremock = WireMockServer(WireMockConfiguration().port(8070).notifier(ConsoleNotifier(true)))

    @Bean
    fun `launch wiremock`(): WireMockServer {
        wiremock.start()
        return wiremock
    }

    @PreDestroy
    fun `stop wiremock`() {
        wiremock.stop()
    }
}