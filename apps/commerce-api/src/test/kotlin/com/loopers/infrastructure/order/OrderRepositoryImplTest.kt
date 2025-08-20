package com.loopers.infrastructure.order

import com.loopers.domain.order.Order
import com.loopers.domain.order.OrderRepository
import com.loopers.domain.order.vo.OrderStatusType
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

class OrderRepositoryImplTest(
    private val cut: OrderRepository,
    private val jpaOrderRepository: JpaOrderRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class Save {
        @Test
        fun `주문 정보를 저장할 수 있다`() {
            // given
            val order = Order(1L, BigDecimal("50000"), BigDecimal("50000"), OrderStatusType.READY)

            // when
            cut.save(order)

            // then
            val actual = jpaOrderRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `주문 정보를 저장 후 해당 주문을 조회하면 동일한 정보를 조회할 수 있다`() {
            // given
            val order = Order(1L, BigDecimal("50000"), BigDecimal("50000"), OrderStatusType.READY)

            // when
            cut.save(order)

            // then
            val actual = jpaOrderRepository.findByIdOrNull(order.id)
            assertThat(actual).isNotNull
                .extracting("userId", "totalAmount", "orderNumber")
                .containsExactly(1L, BigDecimal("50000.00"), order.orderNumber)
        }
    }
}
