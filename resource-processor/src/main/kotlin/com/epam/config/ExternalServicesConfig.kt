package com.epam.config

import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableDiscoveryClient
class ExternalServicesConfig {
    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}