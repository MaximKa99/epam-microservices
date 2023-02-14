package com.epam.cucumber

import com.epam.container.PostgresContainer
import com.fasterxml.jackson.databind.ObjectMapper
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ContextConfiguration(
        initializers = [PostgresContainer::class]
)
@DirtiesContext
class SpringCucumberBase {
    @LocalServerPort
    private lateinit var port: String
    @Autowired
    private lateinit var mapper: ObjectMapper

    private val restTemplate = RestTemplate()

    fun <T: Any> executePost(body: String, url: String, clazz: Class<T>): T? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        return mapper.readValue(
                restTemplate.postForObject(
                        URI.create("http://localhost:$port/api/songs"),
                        HttpEntity(body, headers),
                        String::class.java),
                clazz
        )
    }
}