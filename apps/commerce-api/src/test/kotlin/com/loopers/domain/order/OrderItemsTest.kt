package com.loopers.domain.order

import com.loopers.fixture.order.OrderFixture
import com.loopers.fixture.order.OrderItemFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OrderItemsTest {

    @Nested
    inner class `주문 아이템을 추가할 때` {

        @Test
        fun `여러 주문 아이템을 한번에 추가할 수 있다`() {
            // given
            val orderItems = OrderItems()
            val order = OrderFixture.`3만원 주문`.toEntity()
            val orderItem1 = OrderItemFixture.`상품1`.toEntity(order)
            val orderItem2 = OrderItemFixture.`상품2`.toEntity(order)
            val itemsToAdd = listOf(orderItem1, orderItem2)

            // when
            orderItems.addItems(itemsToAdd)

            // then
            assertThat(orderItems.items).hasSize(2)
                .extracting("productItemId", "productName")
                .containsExactly(
                    tuple(1L, "상품1"),
                    tuple(2L, "상품2"),
                )
        }
    }

    @Nested
    inner class `주문 아이템이 비어있는지 확인할 때` {
        @Test
        fun `주문 아이템이 없으면 true를 반환한다`() {
            // given
            val orderItems = OrderItems()

            // when
            val actual = orderItems.isEmpty()

            // then
            assertThat(actual).isTrue()
        }

        @Test
        fun `주문 아이템이 있으면 false를 반환한다`() {
            // given
            val orderItems = OrderItems()
            val order = OrderFixture.`1만원 주문`.toEntity()
            val orderItem = OrderItemFixture.기본.toEntity(order)
            orderItems.addItems(listOf(orderItem))

            // when
            val actual = orderItems.isEmpty()

            // then
            assertThat(actual).isFalse()
        }
    }
}
