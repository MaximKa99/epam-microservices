package com.epam.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ExternalServicesConfig {
    @Value("\${services.song}")
    private lateinit var songUrl: String
    @Value("\${services.resource}")
    private lateinit var resourceUrl: String

    @Bean("song")
    fun songWebClient() = WebClient.create(songUrl)

    @Bean("resource")
    fun resourceWebClient() = WebClient.create(resourceUrl)
}