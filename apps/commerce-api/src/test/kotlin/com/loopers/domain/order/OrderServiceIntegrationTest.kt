package com.loopers.domain.order

import com.loopers.domain.order.param.SubmitOrderParam
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.infrastructure.order.JpaOrderRepository
import com.loopers.infrastructure.product.JpaProductItemRepository
import com.loopers.infrastructure.product.JpaProductRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

class OrderServiceIntegrationTest(
    private val cut: OrderService,
    private val orderRepository: JpaOrderRepository,
    private val productRepository: JpaProductRepository,
    private val productItemRepository: JpaProductItemRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `주문을 생성할 때` {

        @Test
        fun `주문 시 주문 정보를 저장한다`() {
            // given
            val userId = 1L
            val product = productRepository.save(ProductFixture.`활성 상품 1`.toEntity())
            val productItem = productItemRepository.save(ProductItemFixture.`1 만원 상품`.toEntity(product))

            val param = SubmitOrderParam(userId, listOf(SubmitOrderParam.OrderItem(productItem, 1)), BigDecimal("10000"), BigDecimal("10000"))

            // when
            cut.submitOrder(param)

            // then
            val actual = orderRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("userId", "totalAmount")
                .containsExactly(Tuple.tuple(userId, BigDecimal("10000.00")))
        }

        @Test
        fun `여러 상품 아이템으로 주문하면 총액이 계산되어 저장된다`() {
            // given
            val userId = 1L
            val product = productRepository.save(ProductFixture.`활성 상품 1`.toEntity())
            val productItem1 = productItemRepository.save(ProductItemFixture.`1 만원 상품`.toEntity(product))
            val productItem2 = productItemRepository.save(ProductItemFixture.`2 만원 상품`.toEntity(product))
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
        fun `주문 생성 후 데이터베이스에서 조회할 수 있다`() {
            // given
            val userId = 1L
            val product = productRepository.save(ProductFixture.`활성 상품 1`.toEntity())
            val productItem = productItemRepository.save(ProductItemFixture.`1 만원 상품`.toEntity(product))

            val param = SubmitOrderParam(userId, listOf(SubmitOrderParam.OrderItem(productItem, 1)), BigDecimal("10000"), BigDecimal("10000"))

            // when
            val order = cut.submitOrder(param)

            // then
            val actual = orderRepository.findByIdOrNull(1)
            assertThat(actual).isNotNull
                .extracting("userId", "totalAmount", "orderNumber")
                .containsExactly(1L, BigDecimal("10000.00"), order.orderNumber)
        }
    }
}
