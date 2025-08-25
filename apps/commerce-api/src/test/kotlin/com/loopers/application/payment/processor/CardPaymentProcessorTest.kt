package com.loopers.application.payment.processor

import com.loopers.application.payment.command.ProcessCardPayCommand
import com.loopers.domain.order.fake.TestOrderService
import com.loopers.domain.order.vo.OrderStatusType
import com.loopers.domain.payment.fake.TestPaymentService
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.fixture.order.OrderFixture
import com.loopers.infrastructure.payment.client.PaymentClientFacade
import com.loopers.infrastructure.payment.client.fake.TestPaymentClient
import com.loopers.support.enums.payment.CardType
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Duration

class CardPaymentProcessorTest {
    @Nested
    inner class `결제 가능한 타입 여부를 확인할 때` {
        @Test
        fun `카드 결제 타입이면 true 를 반환한다`() {
            // given
            val orderService = TestOrderService()
            val paymentService = TestPaymentService()
            val paymentClient = PaymentClientFacade(TestPaymentClient())
            val cut = CardPaymentProcessor(orderService, paymentService, paymentClient)

            // when
            val result = cut.support(PaymentType.CARD)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `카드 결제 타입이 아니면 false 를 반환한다`() {
            // given
            val orderService = TestOrderService()
            val paymentService = TestPaymentService()
            val paymentClient = PaymentClientFacade(TestPaymentClient())
            val cut = CardPaymentProcessor(orderService, paymentService, paymentClient)

            // when
            val result = cut.support(PaymentType.POINT)

            // then
            assertThat(result).isFalse()
        }
    }

    @Nested
    inner class `결제를 진행할 때` {
        @Test
        fun `카드 결제 성공시 결제 보류 기록한다`() {
            // given
            val testPaymentService = TestPaymentService()
            val orderService = TestOrderService()
            val paymentService = spyk(testPaymentService)
            val testPaymentClient = TestPaymentClient()
            val paymentClient = PaymentClientFacade(testPaymentClient)
            val cut = CardPaymentProcessor(orderService, paymentService, paymentClient)

            val order = OrderFixture.기본.toEntity()
            orderService.addOrders(listOf(order))
            testPaymentClient.setFailure(false)

            val command = ProcessCardPayCommand(
                userId = 1L,
                orderNumber = order.orderNumber,
                cardType = CardType.SAMSUNG,
                cardNo = "1234-5678-9012-3456",
            )

            // when
            cut.process(command)

            // then
            Awaitility.await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted {
                    verify(exactly = 1) {
                        paymentService.recordPendingPayment(
                            match {
                                it.orderId == order.id &&
                                        it.amount == BigDecimal("10000") &&
                                        it.type == PaymentType.CARD
                            },
                        )
                    }
                }
        }

        @Test
        fun `카드 결제 성공시 주문을 보류 처리한다`() {
            // given
            val testOrderService = TestOrderService()
            val orderService = spyk(testOrderService)
            val paymentService = TestPaymentService()
            val testPaymentClient = TestPaymentClient()
            val paymentClient = PaymentClientFacade(testPaymentClient)
            val cut = CardPaymentProcessor(orderService, paymentService, paymentClient)

            val order = OrderFixture.기본.toEntity()
            testOrderService.addOrders(listOf(order))
            testPaymentClient.setFailure(false)

            val command = ProcessCardPayCommand(
                userId = 1L,
                orderNumber = order.orderNumber,
                cardType = CardType.KB,
                cardNo = "9876-5432-1098-7654",
            )

            // when
            cut.process(command)

            // then
            Awaitility.await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted {
                    assertThat(order.status).isEqualTo(OrderStatusType.PENDING)
                }
        }

        @Test
        fun `카드 결제 실패시 결제 실패를 기록한다`() {
            // given
            val testPaymentService = TestPaymentService()
            val orderService = TestOrderService()
            val paymentService = spyk(testPaymentService)
            val testPaymentClient = TestPaymentClient()
            val paymentClient = PaymentClientFacade(testPaymentClient)
            val cut = CardPaymentProcessor(orderService, paymentService, paymentClient)

            val order = OrderFixture.`3만원 주문`.toEntity()
            orderService.addOrders(listOf(order))
            testPaymentClient.setFailure(true, "카드 잔액 부족")

            val command = ProcessCardPayCommand(
                userId = 2L,
                orderNumber = order.orderNumber,
                cardType = CardType.HYUNDAI,
                cardNo = "1111-2222-3333-4444",
            )

            // when
            cut.process(command)

            // then
            Awaitility.await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted {
                    verify(exactly = 1) {
                        paymentService.recordFailedPayment(
                            match {
                                it.orderId == order.id &&
                                        it.amount == BigDecimal("30000") &&
                                        it.type == PaymentType.CARD
                            },
                        )
                    }
                }
        }

        @Test
        fun `카드 결제 실패시 주문을 취소한다`() {
            // given
            val testOrderService = TestOrderService()
            val orderService = spyk(testOrderService)
            val paymentService = TestPaymentService()
            val testPaymentClient = TestPaymentClient()
            val paymentClient = PaymentClientFacade(testPaymentClient)
            val cut = CardPaymentProcessor(orderService, paymentService, paymentClient)

            val order = spyk(OrderFixture.기본.toEntity())
            testOrderService.addOrders(listOf(order))
            testPaymentClient.setFailure(true, "네트워크 오류")

            val command = ProcessCardPayCommand(
                userId = 1L,
                orderNumber = order.orderNumber,
                cardType = CardType.SAMSUNG,
                cardNo = "5555-6666-7777-8888",
            )

            // when
            cut.process(command)

            // then
            Awaitility.await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted {
                    verify(exactly = 1) { order.cancel() }
                }
        }

        @Test
        fun `카드 결제 요청을 PG 서버로 전송한다`() {
            // given
            val testPaymentClient = TestPaymentClient()
            val orderService = TestOrderService()
            val paymentService = TestPaymentService()
            val paymentClient = spyk(PaymentClientFacade(testPaymentClient))
            val cut = CardPaymentProcessor(orderService, paymentService, paymentClient)

            val order = OrderFixture.기본.toEntity()
            orderService.addOrders(listOf(order))

            val command = ProcessCardPayCommand(
                userId = 1L,
                orderNumber = order.orderNumber,
                cardType = CardType.KB,
                cardNo = "1234-1234-1234-1234",
            )

            // when
            cut.process(command)

            // then
            Awaitility.await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted {
                    verify(exactly = 1) {
                        paymentClient.processPayment(command.toPaymentRequest(order.orderNumber, BigDecimal("10000")))
                    }
                }
        }
    }
}
