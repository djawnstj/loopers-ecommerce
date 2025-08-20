package com.loopers.domain.order

import com.loopers.domain.order.vo.OrderStatusType
import com.loopers.fixture.order.OrderFixture
import com.loopers.fixture.order.OrderItemFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class OrderTest {

    @Nested
    inner class `주문을 생성할 때` {

        @Test
        fun `주문 번호는 UUID로 자동 생성된다`() {
            // given
            mockkStatic(UUID::class)
            val uuid: UUID = mockk()
            every { UUID.randomUUID() } returns uuid
            every { uuid.toString() } returns "uuid"

            // when
            val actual = OrderFixture.기본.toEntity()

            // then
            unmockkStatic(UUID::class)
            assertThat(actual.orderNumber).isEqualTo("uuid")
        }
    }

    @Nested
    inner class `주문 아이템을 추가할 때` {
        @Test
        fun `빈 주문 아이템 리스트를 추가하면 CoreException REQUIRED_NOT_EMPTY_ORDER_ITEMS 예외를 던진다`() {
            // given
            val order = OrderFixture.기본.toEntity()
            val emptyOrderItems = emptyList<OrderItem>()

            // when then
            assertThatThrownBy {
                order.addItem(emptyOrderItems)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.REQUIRED_NOT_EMPTY_ORDER_ITEMS, "주문 시 주문 아이템은 1개 이상이어야 합니다.")
        }

        @Test
        fun `여러 주문 아이템을 한번에 추가할 수 있다`() {
            // given
            val order = OrderFixture.`3만원 주문`.toEntity()
            val orderItem1 = OrderItemFixture.`상품1`.toEntity(order)
            val orderItem2 = OrderItemFixture.`상품2`.toEntity(order)
            val orderItems = listOf(orderItem1, orderItem2)

            // when
            order.addItem(orderItems)

            // then
            assertThat(order.orderItems.items).hasSize(2)
                .extracting("productItemId", "productName", "productPrice")
                .containsExactly(
                    tuple(1L, "상품1", BigDecimal("10000")),
                    tuple(2L, "상품2", BigDecimal("20000")),
                )
        }
    }

    @Nested
    inner class `주문을 완료할 때` {
        @Test
        fun `주문 상태가 COMPLETE 로 변경된다`() {
            // given
            val order = OrderFixture.기본.toEntity()
            assertThat(order.status).isEqualTo(OrderStatusType.READY)

            // when
            order.complete()

            // then
            assertThat(order.status).isEqualTo(OrderStatusType.COMPLETE)
        }
    }

    @Nested
    inner class `주문을 취소할 때` {
        @Test
        fun `주문 상태가 CANCELED 로 변경된다`() {
            // given
            val order = OrderFixture.기본.toEntity()
            assertThat(order.status).isEqualTo(OrderStatusType.READY)

            // when
            order.cancel()

            // then
            assertThat(order.status).isEqualTo(OrderStatusType.CANCELED)
        }
    }
}
