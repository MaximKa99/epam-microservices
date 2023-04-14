package com.epam.route

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RouteConfig(
) {
    @Bean
    fun songRoute(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route {
                it
                    .path("/song/**")
                    .filters {f ->
                        f.rewritePath("/song/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://SONG")
            }.build()
    }

    @Bean
    fun resourceRoute(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route {
                it
                    .path("/resource/**")
                    .filters {f ->
                        f.rewritePath("/resource/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://RESOURCE")
            }.build()
    }

    @Bean
    fun resourceProcessorRoute(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route {
                it
                    .path("/resource-processor/**")
                    .filters {f ->
                        f.rewritePath("/resource-processor/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://RESOURCE-PROCESSOR")
            }.build()
    }

    @Bean
    fun storageRoute(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route {
                it
                    .path("/storage/**")
                    .filters { f ->
                        f.rewritePath("/resource-processor/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://STORAGE")
            }.build()
    }
}