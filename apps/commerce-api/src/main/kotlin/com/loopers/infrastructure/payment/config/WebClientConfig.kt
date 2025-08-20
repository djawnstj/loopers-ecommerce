package com.loopers.infrastructure.payment.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
class WebClientConfig {

    @Value("\${pg-simulator.base-url:http://localhost:8082}")
    private lateinit var pgSimulatorBaseUrl: String

    @Bean
    fun pgWebClient(): WebClient {
        val connectionProvider = ConnectionProvider.builder("pg-client")
            .maxConnections(50)
            .maxIdleTime(Duration.ofSeconds(30))
            .maxLifeTime(Duration.ofSeconds(120))
            .pendingAcquireTimeout(Duration.ofSeconds(10))
            .evictInBackground(Duration.ofSeconds(60))
            .build()

        val httpClient = HttpClient.create(connectionProvider)
            .responseTimeout(Duration.ofSeconds(10))

        return WebClient.builder()
            .baseUrl(pgSimulatorBaseUrl)
            .clientConnector(org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
            .defaultHeader("Content-Type", "application/json")
            .build()
    }
}
