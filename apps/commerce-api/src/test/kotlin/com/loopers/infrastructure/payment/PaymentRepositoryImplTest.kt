package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentRepository
import com.loopers.domain.payment.vo.PaymentType
import com.loopers.fixture.payment.PaymentFixture
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class PaymentRepositoryImplTest(
    private val cut: PaymentRepository,
    private val jpaPaymentRepository: JpaPaymentRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class Save {
        @Test
        fun `결제 정보를 저장할 수 있다`() {
            // given
            val payment = PaymentFixture.`1만원 포인트 결제 성공`.toEntity()

            // when
            cut.save(payment)

            // then
            val actual = jpaPaymentRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `결제 정보를 저장 후 해당 결제를 조회하면 동일한 정보를 조회할 수 있다`() {
            // given
            val payment = PaymentFixture.`1만원 포인트 결제 성공`.toEntity()

            // when
            cut.save(payment)

            // then
            val actual = jpaPaymentRepository.findByIdOrNull(payment.id)
            assertThat(actual).isNotNull
                .extracting("type")
                .isEqualTo(PaymentType.POINT)
        }
    }
}
