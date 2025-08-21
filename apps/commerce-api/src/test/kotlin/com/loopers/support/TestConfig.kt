package com.loopers.support

import com.loopers.infrastructure.payment.client.fake.TestPaymentClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestConfig {
    @Bean
    @Primary
    fun testPaymentClient(): TestPaymentClient {
        return TestPaymentClient()
    }
}
