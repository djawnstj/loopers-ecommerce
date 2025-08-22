package com.loopers.domain.order

import com.loopers.domain.order.param.SubmitOrderParam
import com.loopers.domain.product.ProductItem
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.infrastructure.order.fake.TestOrderRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderServiceImplTest {

    @Nested
    inner class `주문을 생성할 때` {
        @Test
        fun `빈 상품 아이템 목록으로 주문을 생성하면 CoreException REQUIRED_NOT_EMPTY_ORDER_ITEMS 예외를 던진다`() {
            // given
            val orderRepository = TestOrderRepository()
            val cut = OrderServiceImpl(orderRepository)

            val param = SubmitOrderParam(1L, listOf(), BigDecimal("10000"), BigDecimal("10000"))

            // when then
            assertThatThrownBy {
                cut.submitOrder(param)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.REQUIRED_NOT_EMPTY_ORDER_ITEMS, "주문 시 주문 아이템은 1개 이상이어야 합니다.")
        }

        @Test
        fun `주문 후 생성된 주문 정보를 반환한다`() {
            // given
            val orderRepository = TestOrderRepository()
            val cut = OrderServiceImpl(orderRepository)

            val userId = 1L
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val productItem = ProductItem(product, "상품", BigDecimal("15000"), 1)

            val param = SubmitOrderParam(userId, listOf(SubmitOrderParam.OrderItem(productItem, 1)), BigDecimal("15000"), BigDecimal("15000"))

            // when
            val actual = cut.submitOrder(param)

            // then
            assertThat(actual)
                .extracting("userId", "totalAmount")
                .containsExactly(1L, BigDecimal("15000"))
        }

        @Test
        fun `상품 아이템들의 가격 합계로 주문 총액을 계산한다`() {
            // given
            val orderRepository = TestOrderRepository()
            val cut = OrderServiceImpl(orderRepository)

            val userId = 1L
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val productItem1 = ProductItemFixture.`1 만원 상품`.toEntity(product)
            val productItem2 = ProductItemFixture.`2 만원 상품`.toEntity(product)
            val totalAmount = BigDecimal("30000")
            val payPrice = BigDecimal("30000")

            val param = SubmitOrderParam(
                userId,
                listOf(
                    SubmitOrderParam.OrderItem(productItem1, 1),
                    SubmitOrderParam.OrderItem(productItem2, 1),
                ),
                totalAmount,
                payPrice,
            )

            // when
            val actual = cut.submitOrder(param)

            // then
            assertThat(actual.totalAmount.value).isEqualByComparingTo(BigDecimal("30000"))
        }

        @Test
        fun `주문 시 주문 정보를 저장한다`() {
            // given
            val orderRepository = spyk(TestOrderRepository())
            val cut = OrderServiceImpl(orderRepository)

            val userId = 1L
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val productItem = ProductItemFixture.`1 만원 상품`.toEntity(product)
            val totalAmount = BigDecimal("10000")
            val payPrice = BigDecimal("10000")

            val param = SubmitOrderParam(userId, listOf(SubmitOrderParam.OrderItem(productItem, 1)), totalAmount, payPrice)

            // when
            cut.submitOrder(param)

            // then
            verify(exactly = 1) {
                orderRepository.save(
                    match {
                        it.userId == 1L &&
                                it.totalAmount.value.compareTo(BigDecimal("10000")) == 0
                    },
                )
            }
        }

        @Test
        fun `주문 시 상품 아이템들이 주문 아이템으로 추가된다`() {
            // given
            val orderRepository = TestOrderRepository()
            val cut = OrderServiceImpl(orderRepository)

            val userId = 1L
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val productItem1 = ProductItemFixture.`1 만원 상품`.toEntity(product)
            val productItem2 = ProductItemFixture.`2 만원 상품`.toEntity(product)
            val totalAmount = BigDecimal("10000")
            val payPrice = BigDecimal("10000")

            val param = SubmitOrderParam(
                userId,
                listOf(
                    SubmitOrderParam.OrderItem(productItem1, 1),
                    SubmitOrderParam.OrderItem(productItem2, 1),
                ),
                totalAmount,
                payPrice,
            )

            // when
            val actual = cut.submitOrder(param)

            // then
            assertThat(actual.orderItems.items).hasSize(2)
                .extracting("productName", "productPrice", "quantity")
                .containsExactlyInAnyOrder(
                    tuple("검은색 라지", BigDecimal("10000"), 1),
                    tuple("검은색 라지", BigDecimal("20000"), 1),
                )
        }
    }
}
