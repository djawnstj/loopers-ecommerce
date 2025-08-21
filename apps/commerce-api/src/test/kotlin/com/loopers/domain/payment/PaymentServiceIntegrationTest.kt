package com.loopers.domain.payment

import com.loopers.domain.payment.param.RecordFailedPaymentParam
import com.loopers.domain.payment.param.RecordPaidPaymentParam
import com.loopers.domain.payment.vo.PaymentStatusType
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.infrastructure.payment.JpaPaymentRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

class PaymentServiceIntegrationTest(
    private val cut: PaymentService,
    private val paymentRepository: JpaPaymentRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `결제 성공 기록할 때` {
        @Test
        fun `결제 성공 정보를 저장한다`() {
            // given
            val param = RecordPaidPaymentParam(
                orderId = 1L,
                amount = BigDecimal("10000"),
                paymentKey = "paymentKey",
                type = PaymentType.CARD,
            )

            // when
            cut.recordPaidPayment(param)

            // then
            val actual = paymentRepository.findByIdOrNull(1)
            assertThat(actual)
                .extracting("orderId", "amount", "type", "status")
                .containsExactly(1L, BigDecimal("10000.00"), PaymentType.CARD, PaymentStatusType.PAID)
        }
    }

    @Nested
    inner class `결제 실패 기록할 때` {

        @Test
        fun `결제 실패 정보를 저장한다`() {
            // given
            val param = RecordFailedPaymentParam(
                orderId = 6L,
                amount = BigDecimal("30000"),
                paymentKey = "paymentKey",
                type = PaymentType.CARD,
            )

            // when
            cut.recordFailedPayment(param)

            // then
            val actual = paymentRepository.findByIdOrNull(1L)
            assertThat(actual).isNotNull
                .extracting("orderId", "amount", "type", "status")
                .containsExactly(6L, BigDecimal("30000.00"), PaymentType.CARD, PaymentStatusType.FAILED)
        }
    }
}
