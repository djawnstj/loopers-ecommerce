package com.loopers.infrastructure.payment.config

import com.loopers.infrastructure.payment.client.PaymentClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class PaymentHttpInterfaceConfig(
    @Qualifier("pgWebClient") private val pgWebClient: WebClient
) {

    @Bean
    fun paymentHttpInterfaceClient(): PaymentClient {
        val factory = HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(pgWebClient))
            .build()

        return factory.createClient(PaymentClient::class.java)
    }
}
