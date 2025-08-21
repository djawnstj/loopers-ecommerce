package com.loopers.infrastructure.payment.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfig {

    @Value("\${pg-simulator.base-url:http://localhost:8082}")
    private lateinit var pgSimulatorBaseUrl: String

    @Bean
    fun pgWebClient(): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(3, TimeUnit.SECONDS))
            }

        return WebClient.builder()
            .baseUrl(pgSimulatorBaseUrl)
            .clientConnector(org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
            .defaultHeader("Content-Type", "application/json")
            .build()
    }
}
