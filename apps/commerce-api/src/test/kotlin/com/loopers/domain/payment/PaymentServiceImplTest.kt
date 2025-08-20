package com.loopers.domain.payment

import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.infrastructure.payment.fake.TestPaymentRepository
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
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
}
