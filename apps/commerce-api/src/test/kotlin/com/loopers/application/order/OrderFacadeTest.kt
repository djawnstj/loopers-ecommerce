package com.loopers.application.order

import com.loopers.application.order.command.CreateOrderCommand
import com.loopers.domain.coupon.fake.TestUserCouponService
import com.loopers.domain.order.fake.TestOrderService
import com.loopers.domain.product.fake.TestProductService
import com.loopers.domain.user.fake.TestUserService
import com.loopers.fixture.coupon.CouponFixture
import com.loopers.fixture.coupon.UserCouponFixture
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.fixture.user.UserFixture
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderFacadeTest {

    @Nested
    inner class `주문을 생성할 때` {

        @Test
        fun `주문이 생성된다`() {
            // given
            val userService = TestUserService()
            val productService = TestProductService()
            val userCouponService = TestUserCouponService()
            val orderService = spyk(TestOrderService())
            val cut = OrderFacade(userService, productService, userCouponService, orderService)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val productItem = ProductItemFixture.`검은색 라지 만원`.toEntity(product)
            productService.addProducts(listOf(product))
            productService.addProductItems(listOf(productItem))

            val command = CreateOrderCommand(
                "loginId",
                listOf(
                    CreateOrderCommand.OrderItemSummary(productItem.id, 1),
                ),
            )

            // when
            cut.createOrder(command)

            // then
            verify { orderService.submitOrder(match { it.userId == 1L }) }
        }

        @Test
        fun `쿠폰을 사용할 수 있다`() {
            // given
            val userService = TestUserService()
            val productService = TestProductService()
            val userCouponService = spyk(TestUserCouponService())
            val orderService = TestOrderService()
            val cut = OrderFacade(userService, productService, userCouponService, orderService)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))

            val coupon = CouponFixture.`고정 할인 5000원 쿠폰`.toEntity()
            userCouponService.addUserCoupon(UserCouponFixture.기본.toEntity(coupon, user.id))

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val productItem = ProductItemFixture.`검은색 라지 만원`.toEntity(product)
            productService.addProducts(listOf(product))
            productService.addProductItems(listOf(productItem))

            val command = CreateOrderCommand(
                "loginId",
                listOf(
                    CreateOrderCommand.OrderItemSummary(productItem.id, 1),
                ),
                coupon.id,
            )

            // when
            cut.createOrder(command)

            // then
            verify { userCouponService.calculatePayPrice(match { it.userId == user.id && it.couponId == coupon.id && it.totalAmount == BigDecimal("10000") }) }
        }
    }
}
