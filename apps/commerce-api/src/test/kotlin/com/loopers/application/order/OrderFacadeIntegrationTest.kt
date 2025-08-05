package com.loopers.application.order

import com.loopers.application.order.command.CreateOrderCommand
import com.loopers.domain.point.vo.Point
import com.loopers.domain.product.vo.Quantity
import com.loopers.fixture.brand.BrandFixture
import com.loopers.fixture.point.UserPointFixture
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.infrastructure.brand.JpaBrandRepository
import com.loopers.infrastructure.order.JpaOrderRepository
import com.loopers.infrastructure.point.JpaUserPointRepository
import com.loopers.infrastructure.product.JpaProductItemRepository
import com.loopers.infrastructure.product.JpaProductRepository
import com.loopers.infrastructure.user.JpaUserRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

class OrderFacadeIntegrationTest(
    private val cut: OrderFacade,
    private val userRepository: JpaUserRepository,
    private val brandRepository: JpaBrandRepository,
    private val productRepository: JpaProductRepository,
    private val productItemRepository: JpaProductItemRepository,
    private val userPointRepository: JpaUserPointRepository,
    private val orderRepository: JpaOrderRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `주문을 생성할 때` {

        @Test
        fun `주문이 생성된다`() {
            // given
            val user = userRepository.save(UserFixture.기본.toEntity())
            val brand = brandRepository.save(BrandFixture.기본.toEntity())
            val product = productRepository.save(ProductFixture.`활성 상품 1`.toEntity(brand.id))
            val productItem = productItemRepository.save(ProductItemFixture.`검은색 라지 만원`.toEntity(product))

            val userPoint = UserPointFixture.`1000 포인트`.toEntity(user.id)
            userPoint.charge(Point(BigDecimal("10000")))
            userPointRepository.save(userPoint)

            val command = CreateOrderCommand(
                user.loginId.value,
                listOf(CreateOrderCommand.OrderItemSummary(productItem.id, 1)),
            )

            // when
            cut.createOrder(command)

            // then
            val actual = orderRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("userId", "payPrice")
                .containsExactly(Tuple.tuple(user.id, BigDecimal("10000.00")))
        }

        @Test
        fun `포인트가 차감된다`() {
            // given
            val user = userRepository.save(UserFixture.기본.toEntity())
            val brand = brandRepository.save(BrandFixture.기본.toEntity())
            val product = productRepository.save(ProductFixture.`활성 상품 1`.toEntity(brand.id))
            val productItem = productItemRepository.save(ProductItemFixture.`검은색 라지 만원`.toEntity(product))

            val userPoint = UserPointFixture.`1000 포인트`.toEntity(user.id)
            userPoint.charge(Point(BigDecimal("15000")))
            userPointRepository.save(userPoint)

            val command = CreateOrderCommand(
                user.loginId.value,
                listOf(CreateOrderCommand.OrderItemSummary(productItem.id, 1)),
            )

            // when
            cut.createOrder(command)

            // then
            val actual = userPointRepository.findByIdOrNull(1L)!!
            assertThat(actual.balance.value).isEqualByComparingTo(BigDecimal("6000"))
        }

        @Test
        fun `상품 재고가 차감된다`() {
            // given
            val user = userRepository.save(UserFixture.기본.toEntity())
            val brand = brandRepository.save(BrandFixture.기본.toEntity())
            val product = productRepository.save(ProductFixture.`활성 상품 1`.toEntity(brand.id))
            val productItem = productItemRepository.save(ProductItemFixture.`검은색 라지 만원`.toEntity(product))

            val userPoint = UserPointFixture.`1000 포인트`.toEntity(user.id)
            userPoint.charge(Point(BigDecimal("30000")))
            userPointRepository.save(userPoint)

            val command = CreateOrderCommand(
                user.loginId.value,
                listOf(CreateOrderCommand.OrderItemSummary(productItem.id, 3)),
            )

            // when
            cut.createOrder(command)

            // then
            val actual = productItemRepository.findById(productItem.id).get()
            assertThat(actual.quantity).isEqualTo(Quantity(7))
        }
    }
}
