package com.loopers.application.order

import com.loopers.application.order.command.CreateOrderCommand
import com.loopers.fixture.brand.BrandFixture
import com.loopers.fixture.coupon.CouponFixture
import com.loopers.fixture.coupon.UserCouponFixture
import com.loopers.fixture.point.UserPointFixture
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.infrastructure.brand.JpaBrandRepository
import com.loopers.infrastructure.coupon.JpaCouponRepository
import com.loopers.infrastructure.coupon.JpaUserCouponRepository
import com.loopers.infrastructure.order.JpaOrderRepository
import com.loopers.infrastructure.point.JpaUserPointRepository
import com.loopers.infrastructure.product.JpaProductItemRepository
import com.loopers.infrastructure.product.JpaProductRepository
import com.loopers.infrastructure.user.JpaUserRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.TransactionTraceHolder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class OrderFacadeIntegrationTest(
    private val cut: OrderFacade,
    private val userRepository: JpaUserRepository,
    private val brandRepository: JpaBrandRepository,
    private val productRepository: JpaProductRepository,
    private val productItemRepository: JpaProductItemRepository,
    private val userPointRepository: JpaUserPointRepository,
    private val orderRepository: JpaOrderRepository,
    private val couponRepository: JpaCouponRepository,
    private val userCouponRepository: JpaUserCouponRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `주문을 생성할 때` {

        @Test
        fun `없는 쿠폰으로 주문 요청 시 주문이 생성되지 않는다`() {
            // given
            val user = userRepository.save(UserFixture.기본.toEntity())
            val brand = brandRepository.save(BrandFixture.기본.toEntity())
            val product = productRepository.save(ProductFixture.`활성 상품 1`.toEntity(brand.id))
            val productItem = productItemRepository.save(ProductItemFixture.`검은색 라지 만원`.toEntity(product))
            userPointRepository.save(UserPointFixture.`5만 포인트`.toEntity(user.id))

            val nonExistentCouponId = 999L
            val command = CreateOrderCommand(
                user.loginId.value,
                listOf(CreateOrderCommand.OrderItemSummary(productItem.id, 1)),
                nonExistentCouponId,
            )

            // when
            assertThatThrownBy {
                cut.createOrder(command)
            }

            // then
            val orders = orderRepository.findAll()
            assertAll(
                { assertThat(orders).isEmpty() },
                { assertThat(TransactionTraceHolder.get().rollbackOnly).isTrue() },
            )
        }

        @Test
        fun `사용한 쿠폰으로 주문 요청 시 주문이 생성되지 않는다`() {
            // given
            val user = userRepository.save(UserFixture.기본.toEntity())
            val brand = brandRepository.save(BrandFixture.기본.toEntity())
            val product = productRepository.save(ProductFixture.`활성 상품 1`.toEntity(brand.id))
            val productItem = productItemRepository.save(ProductItemFixture.`검은색 라지 만원`.toEntity(product))
            userPointRepository.save(UserPointFixture.`5만 포인트`.toEntity(user.id))

            val coupon = couponRepository.save(CouponFixture.`고정 할인 5000원 쿠폰`.toEntity())
            val userCoupon = UserCouponFixture.기본.toEntity(coupon = coupon, userId = user.id)
            userCoupon.use(BigDecimal("10000"))
            userCouponRepository.save(userCoupon)

            val command = CreateOrderCommand(
                user.loginId.value,
                listOf(CreateOrderCommand.OrderItemSummary(productItem.id, 1)),
                coupon.id,
            )

            // when
            assertThatThrownBy {
                cut.createOrder(command)
            }

            // then
            val orders = orderRepository.findAll()
            assertAll(
                { assertThat(orders).isEmpty() },
                { assertThat(TransactionTraceHolder.get().rollbackOnly).isTrue() },
            )
        }

        @Test
        fun `주문이 생성된다`() {
            // given
            val user = userRepository.save(UserFixture.기본.toEntity())
            val brand = brandRepository.save(BrandFixture.기본.toEntity())
            val product = productRepository.save(ProductFixture.`활성 상품 1`.toEntity(brand.id))
            val productItem = productItemRepository.save(ProductItemFixture.`검은색 라지 만원`.toEntity(product))

            val userPoint = UserPointFixture.`1만 5천 포인트`.toEntity(user.id)
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
        fun `서로 다른 순서로 같은 상품들을 동시에 주문해도 정상적으로 주문에 성공한다`() {
            // given
            val user1 = userRepository.save(UserFixture.`로그인 ID 1`.toEntity())
            val user2 = userRepository.save(UserFixture.`로그인 ID 2`.toEntity())
            val brand = brandRepository.save(BrandFixture.기본.toEntity())
            val product1 = productRepository.save(ProductFixture.`활성 상품 1`.toEntity(brand.id))
            val product2 = productRepository.save(ProductFixture.`활성 상품 2`.toEntity(brand.id))
            val productItem1 = productItemRepository.save(ProductItemFixture.`검은색 라지 만원`.toEntity(product1))
            val productItem2 = productItemRepository.save(ProductItemFixture.`빨간색 라지 만원`.toEntity(product2))

            userPointRepository.saveAndFlush(UserPointFixture.`5만 포인트`.toEntity(user1.id))
            userPointRepository.saveAndFlush(UserPointFixture.`5만 포인트`.toEntity(user2.id))

            val command1 = CreateOrderCommand(
                user1.loginId.value,
                listOf(
                    CreateOrderCommand.OrderItemSummary(productItem1.id, 1),
                    CreateOrderCommand.OrderItemSummary(productItem2.id, 1),
                ),
            )
            val command2 = CreateOrderCommand(
                user2.loginId.value,
                listOf(
                    CreateOrderCommand.OrderItemSummary(productItem2.id, 1),
                    CreateOrderCommand.OrderItemSummary(productItem1.id, 1),
                ),
            )

            val threadCount = 2
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)

            // when
            listOf(command1, command2).forEach { command ->
                executor.submit {
                    try {
                        cut.createOrder(command)
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await(10, TimeUnit.SECONDS)
            executor.shutdown()

            val actual = orderRepository.findAll()
            assertThat(actual).hasSize(2)
                .extracting("userId", "payPrice")
                .containsExactlyInAnyOrder(
                    Tuple.tuple(user1.id, BigDecimal("20000.00")),
                    Tuple.tuple(user2.id, BigDecimal("20000.00")),
                )
        }

        @Test
        fun `같은 유저가 동시에 동일한 쿠폰으로 두 번 주문하면 쿠폰은 한 번만 적용된다`() {
            // given
            val user = userRepository.save(UserFixture.기본.toEntity())
            val brand = brandRepository.save(BrandFixture.기본.toEntity())
            val product1 = productRepository.save(ProductFixture.`활성 상품 1`.toEntity(brand.id))
            val product2 = productRepository.save(ProductFixture.`활성 상품 2`.toEntity(brand.id))
            val productItem1 = productItemRepository.save(ProductItemFixture.`검은색 라지 만원`.toEntity(product1))
            val productItem2 = productItemRepository.save(ProductItemFixture.`빨간색 라지 만원`.toEntity(product2))

            val coupon = couponRepository.save(CouponFixture.`고정 할인 5000원 쿠폰`.toEntity())
            userCouponRepository.saveAndFlush(UserCouponFixture.기본.toEntity(coupon = coupon, userId = user.id))
            userPointRepository.saveAndFlush(UserPointFixture.`5만 포인트`.toEntity(user.id))

            val command1 = CreateOrderCommand(
                user.loginId.value,
                listOf(CreateOrderCommand.OrderItemSummary(productItem1.id, 1)),
                coupon.id,
            )
            val command2 = CreateOrderCommand(
                user.loginId.value,
                listOf(CreateOrderCommand.OrderItemSummary(productItem2.id, 1)),
                coupon.id,
            )

            val threadCount = 2
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)

            // when
            listOf(command1, command2).forEach { command ->
                executor.submit {
                    try {
                        cut.createOrder(command)
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await(10, TimeUnit.SECONDS)
            executor.shutdown()

            // then
            val actual = orderRepository.findAll()
            assertThat(actual).hasSize(1)
        }
    }
}
