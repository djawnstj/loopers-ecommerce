package com.loopers.domain.payment

import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import com.loopers.domain.payment.param.RecordPendingPaymentParam
import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.infrastructure.payment.fake.TestPaymentRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentServiceImplTest {

    @Nested
    inner class `결제 성공 기록할 때` {
        @Test
        fun `결제 성공 정보를 저장한다`() {
            // given
            val paymentRepository = spyk(TestPaymentRepository())
            val cut = PaymentServiceImpl(paymentRepository)

            val param = RecordPaidPaymentParam(
                orderId = 1L,
                null,
                amount = BigDecimal("10000"),
                type = PaymentType.CARD
            )

            // when
            cut.recordPaidPayment(param)

            // then
            verify(exactly = 1) {
                paymentRepository.save(
                    match {
                        it.orderId == 1L &&
                                it.amount.compareTo(BigDecimal("10000")) == 0 &&
                                it.type == PaymentType.CARD &&
                                it.status == PaymentStatusType.PAID
                    }
                )
            }
        }
    }

    @Nested
    inner class `결제 실패 기록할 때` {
        @Test
        fun `결제 실패 정보를 저장한다`() {
            // given
            val paymentRepository = spyk(TestPaymentRepository())
            val cut = PaymentServiceImpl(paymentRepository)

            val param = RecordFailedPaymentParam(
                orderId = 2L,
                null,
                amount = BigDecimal("20000"),
                type = PaymentType.CARD
            )

            // when
            cut.recordFailedPayment(param)

            // then
            verify(exactly = 1) {
                paymentRepository.save(
                    match {
                        it.orderId == 2L &&
                                it.amount.compareTo(BigDecimal("20000")) == 0 &&
                                it.type == PaymentType.CARD &&
                                it.status == PaymentStatusType.FAILED
                    }
                )
            }
        }
    }

    @Nested
    inner class `결제 대기 기록할 때` {
        @Test
        fun `결제 대기 정보를 저장한다`() {
            // given
            val paymentRepository = spyk(TestPaymentRepository())
            val cut = PaymentServiceImpl(paymentRepository)

            val param = RecordPendingPaymentParam(
                orderId = 4L,
                paymentKey = "pay_pending123",
                amount = BigDecimal("20000"),
                type = PaymentType.CARD
            )

            // when
            cut.recordPendingPayment(param)

            // then
            verify(exactly = 1) {
                paymentRepository.save(
                    match {
                        it.orderId == 4L &&
                                it.paymentKey == "pay_pending123" &&
                                it.amount.compareTo(BigDecimal("20000")) == 0 &&
                                it.type == PaymentType.CARD &&
                                it.status == PaymentStatusType.PENDING
                    }
                )
            }
        }
    }

    @Nested
    inner class `결제키로 결제 정보를 조회할 때` {
        @Test
        fun `존재하는 결제키로 조회하면 결제 정보를 반환한다`() {
            // given
            val paymentRepository = spyk(TestPaymentRepository())
            val cut = PaymentServiceImpl(paymentRepository)

            val paymentKey = "pay_test123"
            val param = RecordPaidPaymentParam(
                orderId = 5L,
                paymentKey = paymentKey,
                amount = BigDecimal("30000"),
                type = PaymentType.CARD
            )
            cut.recordPaidPayment(param)

            // when
            val result = cut.getPaymentByPaymentKey(paymentKey)

            // then
            assertThat(result).isNotNull
            assertThat(result!!)
                .extracting("orderId", "paymentKey", "amount", "type", "status")
                .containsExactly(5L, "pay_test123", BigDecimal("30000"), PaymentType.CARD, PaymentStatusType.PAID)
        }

        @Test
        fun `존재하지 않는 결제키로 조회하면 CoreException PAYMENT_NOT_FOUND 예외를 던진다`() {
            // given
            val paymentRepository = spyk(TestPaymentRepository())
            val cut = PaymentServiceImpl(paymentRepository)

            val nonExistentPaymentKey = "pay_nonexistent"

            // when then
            assertThatThrownBy {
                cut.getPaymentByPaymentKey(nonExistentPaymentKey)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.PAYMENT_NOT_FOUND, "결제 정보를 찾을 수 없습니다.")
        }
    }
}
