package com.loopers.infrastructure.payment.client

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import io.github.resilience4j.reactor.retry.RetryOperator
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.time.Duration
import java.util.concurrent.TimeoutException
import kotlin.math.pow

@Component
class PaymentClientFacade(
    private val paymentClient: PaymentClient,
) {

    private val retry = Retry.of(
        "payment-retry",
        RetryConfig.custom<Any>()
            .maxAttempts(3)
            .intervalFunction { attempt ->
                (1000 * 2.0.pow(attempt.toDouble())).toLong() // 지수 백오프: 1초, 2초, 4초 (밀리초)
            }.retryOnException(::shouldCheckExistingPayment)
            .build(),
    )

    private val circuitBreaker = CircuitBreaker.of(
        "payment-circuit-breaker",
        CircuitBreakerConfig.custom()
            .failureRateThreshold(50.0f) // 실패율 50% 이상시 OPEN
            .waitDurationInOpenState(Duration.ofSeconds(30)) // OPEN 상태 30초 유지
            .slidingWindowSize(10) // 최근 10개 호출 기준으로 판단
            .minimumNumberOfCalls(10) // 최소 10번 호출 후 판단
            .permittedNumberOfCallsInHalfOpenState(3) // HALF_OPEN 상태에서 3번 테스트 호출 허용
            .automaticTransitionFromOpenToHalfOpenEnabled(true) // 자동으로 HALF_OPEN 전환
            .build(),
    )

    fun processPayment(userId: Long, request: PaymentRequest): Mono<PaymentResponse> {
        return paymentClient.processPayment(userId, request)
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
    }

    private fun shouldCheckExistingPayment(error: Throwable): Boolean {
        return when (error) {
            is WebClientResponseException -> error.statusCode.is5xxServerError
            is TimeoutException -> true
            is ConnectException -> true
            is SocketTimeoutException -> true
            else -> false
        }
    }
}
