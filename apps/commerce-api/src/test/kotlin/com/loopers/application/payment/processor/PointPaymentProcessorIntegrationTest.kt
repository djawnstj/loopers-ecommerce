package com.loopers.application.payment.processor

import com.loopers.application.payment.command.ProcessPointPayCommand
import com.loopers.domain.order.vo.OrderStatusType
import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.domain.point.UserPointService
import com.loopers.fixture.order.OrderFixture
import com.loopers.infrastructure.order.JpaOrderRepository
import com.loopers.infrastructure.payment.JpaPaymentRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

class PointPaymentProcessorIntegrationTest(
    private val cut: PointPaymentProcessor,
    private val userPointService: UserPointService,
    private val orderRepository: JpaOrderRepository,
    private val paymentRepository: JpaPaymentRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `결제 타입 지원 여부를 확인할 때` {
        @Test
        fun `포인트 타입을 지원한다`() {
            // when
            val result = cut.support(PaymentType.POINT)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `포인트 결제 타입이 아니면 지원하지 않는다`() {
            // when
            val result = cut.support(PaymentType.CARD)

            // then
            assertThat(result).isFalse()
        }
    }

    @Nested
    inner class `포인트 결제 처리할 때` {

        @Test
        fun `포인트가 부족하면 결제를 실패처리하고 주문을 취소한다`() {
            // given
            val order = OrderFixture.`3만원 주문`.toEntity()
            val savedOrder = orderRepository.save(order)

            userPointService.createInitialPoint(2L)
            userPointService.chargePoint(2L, BigDecimal("10000")) // 부족한 포인트

            val command = ProcessPointPayCommand(
                userId = 2L,
                orderNumber = savedOrder.orderNumber,
            )

            // when
            cut.process(command)

            // then
            val userPoint = userPointService.getUserPoint(2L)
            assertThat(userPoint.balance.value).isEqualTo(BigDecimal("10000.00"))

            val updatedOrder = orderRepository.findByIdOrNull(savedOrder.id)
            assertThat(updatedOrder?.status).isEqualTo(OrderStatusType.CANCELED)

            val payments = paymentRepository.findAll()
            assertThat(payments).hasSize(1)
            assertThat(payments[0])
                .extracting("orderId", "amount", "type", "status")
                .containsExactly(savedOrder.id, BigDecimal("30000.00"), PaymentType.POINT, PaymentStatusType.FAILED)
        }

        @Test
        fun `포인트가 충분하면 포인트를 차감하고 주문을 완료한다`() {
            // given
            val order = OrderFixture.기본.toEntity()
            val savedOrder = orderRepository.save(order)

            userPointService.createInitialPoint(1L)
            userPointService.chargePoint(1L, BigDecimal("20000"))

            val command = ProcessPointPayCommand(
                userId = 1L,
                orderNumber = savedOrder.orderNumber
            )

            // when
            cut.process(command)

            // then
            val userPoint = userPointService.getUserPoint(1L)
            assertThat(userPoint.balance.value).isEqualTo(BigDecimal("10000.00"))

            val updatedOrder = orderRepository.findByIdOrNull(savedOrder.id)
            assertThat(updatedOrder?.status).isEqualTo(OrderStatusType.COMPLETE)

            val payments = paymentRepository.findAll()
            assertThat(payments).hasSize(1)
            assertThat(payments[0])
                .extracting("orderId", "amount", "type", "status")
                .containsExactly(savedOrder.id, BigDecimal("10000.00"), PaymentType.POINT, PaymentStatusType.PAID)
        }
    }
}
