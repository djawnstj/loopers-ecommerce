package com.loopers.application.payment.processor

import com.loopers.application.payment.command.ProcessPointPayCommand
import com.loopers.domain.order.fake.TestOrderService
import com.loopers.domain.order.vo.OrderStatusType
import com.loopers.domain.payment.fake.TestPaymentService
import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.domain.point.fake.TestUserPointService
import com.loopers.domain.product.fake.TestProductService
import com.loopers.fixture.order.OrderFixture
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PointPaymentProcessorTest {

    @Nested
    inner class `결제 가능한 타입 여부를 확인할 때` {
        @Test
        fun `포인트 결제 타입이면 true 를 반환 한다`() {
            // given
            val userPointService = TestUserPointService()
            val paymentService = TestPaymentService()
            val orderService = TestOrderService()
            val productService = TestProductService()
            val cut = PointPaymentProcessor(userPointService, paymentService, orderService, productService)

            // when
            val result = cut.support(PaymentType.POINT)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `포인트 결제 타입이 아니면 false 를 반환 한다`() {
            // given
            val userPointService = TestUserPointService()
            val paymentService = TestPaymentService()
            val orderService = TestOrderService()
            val productService = TestProductService()
            val cut = PointPaymentProcessor(userPointService, paymentService, orderService, productService)

            // when
            val result = cut.support(PaymentType.CARD)

            // then
            assertThat(result).isFalse()
        }
    }

    @Nested
    inner class `결제를 진행할 때` {

        @Test
        fun `포인트가 부족하면 결제 실패를 기록한다`() {
            // given
            val userPointService = TestUserPointService()
            val testPaymentService = TestPaymentService()
            val paymentService = spyk(testPaymentService)
            val testOrderService = TestOrderService()
            val orderService = spyk(testOrderService)
            val productService = TestProductService()
            val cut = PointPaymentProcessor(userPointService, paymentService, orderService, productService)

            val order = OrderFixture.`3만원 주문`.toEntity()
            testOrderService.addOrders(listOf(order))
            userPointService.createInitialPoint(2L)
            userPointService.chargePoint(2L, BigDecimal("10000"))

            val command = ProcessPointPayCommand(
                userId = 2L,
                orderNumber = order.orderNumber,
            )

            // when
            cut.process(command)

            // then
            verify(exactly = 1) {
                paymentService.recordFailedPayment(
                    RecordFailedPaymentParam(order.id, null, BigDecimal("30000"), PaymentType.POINT),
                )
            }
        }

        @Test
        fun `포인트가 부족하면 주문을 취소한다`() {
            // given
            val userPointService = TestUserPointService()
            val paymentService = TestPaymentService()
            val testOrderService = TestOrderService()
            val orderService = spyk(testOrderService)
            val productService = TestProductService()
            val cut = PointPaymentProcessor(userPointService, paymentService, orderService, productService)

            val order = OrderFixture.기본.toEntity()
            testOrderService.addOrders(listOf(order))
            userPointService.createInitialPoint(1L)
            userPointService.chargePoint(1L, BigDecimal("5000"))

            val command = ProcessPointPayCommand(
                userId = 1L,
                orderNumber = order.orderNumber,
            )

            // when
            cut.process(command)

            // then
            assertThat(order.status).isEqualTo(OrderStatusType.CANCELED)
        }

        @Test
        fun `포인트가 충분하면 포인트를 차감한다`() {
            // given
            val testUserPointService = TestUserPointService()
            val userPointService = spyk(testUserPointService)
            val paymentService = TestPaymentService()
            val testOrderService = TestOrderService()
            val orderService = spyk(testOrderService)
            val productService = TestProductService()
            val cut = PointPaymentProcessor(userPointService, paymentService, orderService, productService)

            val order = OrderFixture.기본.toEntity()
            testOrderService.addOrders(listOf(order))
            testUserPointService.createInitialPoint(1L)
            testUserPointService.chargePoint(1L, BigDecimal("20000"))

            val command = ProcessPointPayCommand(
                userId = 1L,
                orderNumber = order.orderNumber,
            )

            // when
            cut.process(command)

            // then
            verify(exactly = 1) { userPointService.useUserPoint(1L, BigDecimal("10000")) }
        }

        @Test
        fun `포인트가 충분하면 결제 성공을 기록한다`() {
            // given
            val userPointService = TestUserPointService()
            val testPaymentService = TestPaymentService()
            val paymentService = spyk(testPaymentService)
            val orderService = TestOrderService()
            val productService = TestProductService()
            val cut = PointPaymentProcessor(userPointService, paymentService, orderService, productService)

            val order = OrderFixture.`3만원 주문`.toEntity()
            orderService.addOrders(listOf(order))
            userPointService.createInitialPoint(2L)
            userPointService.chargePoint(2L, BigDecimal("50000"))

            val command = ProcessPointPayCommand(
                userId = 2L,
                orderNumber = order.orderNumber,
            )

            // when
            cut.process(command)

            // then
            verify(exactly = 1) {
                paymentService.recordPaidPayment(
                    RecordPaidPaymentParam(order.id, null, BigDecimal("30000"), PaymentType.POINT),
                )
            }
        }

        @Test
        fun `포인트가 충분하면 주문을 완료한다`() {
            // given
            val userPointService = TestUserPointService()
            val paymentService = TestPaymentService()
            val testOrderService = TestOrderService()
            val orderService = spyk(testOrderService)
            val productService = TestProductService()
            val cut = PointPaymentProcessor(userPointService, paymentService, orderService, productService)

            val order = OrderFixture.기본.toEntity()
            testOrderService.addOrders(listOf(order))
            userPointService.createInitialPoint(1L)
            userPointService.chargePoint(1L, BigDecimal("20000"))

            val command = ProcessPointPayCommand(
                userId = 1L,
                orderNumber = order.orderNumber,
            )

            // when
            cut.process(command)

            // then
            assertThat(order.status).isEqualTo(OrderStatusType.COMPLETE)
        }
    }
}
