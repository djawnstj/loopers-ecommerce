package com.loopers.application.order

import com.loopers.application.order.command.CreateOrderCommand
import com.loopers.domain.order.fake.TestOrderService
import com.loopers.domain.point.fake.TestUserPointService
import com.loopers.domain.point.vo.Point
import com.loopers.domain.product.fake.TestProductService
import com.loopers.domain.user.fake.TestUserService
import com.loopers.fixture.point.UserPointFixture
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.fixture.user.UserFixture
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
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
            val orderService = spyk(TestOrderService())
            val userPointService = TestUserPointService()
            val cut = OrderFacade(userService, productService, orderService, userPointService)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val productItem = ProductItemFixture.`검은색 라지 만원`.toEntity(product)
            productService.addProducts(listOf(product))
            productService.addProductItems(listOf(productItem))

            val userPoint = UserPointFixture.`1000 포인트`.toEntity(user.id)
            userPoint.charge(Point(BigDecimal("19000")))
            userPointService.addUserPoint(userPoint)

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
        fun `포인트를 사용한다`() {
            // given
            val userService = TestUserService()
            val productService = TestProductService()
            val orderService = TestOrderService()
            val userPointService = spyk(TestUserPointService())
            val cut = OrderFacade(userService, productService, orderService, userPointService)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val productItem = ProductItemFixture.`검은색 라지 만원`.toEntity(product)
            productService.addProducts(listOf(product))
            productService.addProductItems(listOf(productItem))

            val userPoint = UserPointFixture.`1000 포인트`.toEntity(user.id)
            userPoint.charge(Point(BigDecimal("19000")))
            userPointService.addUserPoint(userPoint)

            val command = CreateOrderCommand(
                "loginId",
                listOf(CreateOrderCommand.OrderItemSummary(productItem.id, 1),),
            )

            // when
            cut.createOrder(command)

            // then
            verify { userPointService.useUserPoint(1L, BigDecimal("10000")) }
        }

        @Test
        fun `상품 재고를 차감한다`() {
            // given
            val userService = TestUserService()
            val productService = spyk(TestProductService())
            val orderService = TestOrderService()
            val userPointService = TestUserPointService()
            val cut = OrderFacade(userService, productService, orderService, userPointService)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))

            val product = ProductFixture.`활성 상품 1`.toEntity()
            val productItem = ProductItemFixture.`검은색 라지 만원`.toEntity(product)
            productService.addProducts(listOf(product))
            productService.addProductItems(listOf(productItem))

            val userPoint = UserPointFixture.`1000 포인트`.toEntity(user.id)
            userPoint.charge(Point(BigDecimal("19000")))
            userPointService.addUserPoint(userPoint)

            val command = CreateOrderCommand(
                "loginId",
                listOf(CreateOrderCommand.OrderItemSummary(productItem.id, 3)),
            )

            // when
            cut.createOrder(command)

            // then
            verify { productService.deductProductItemsQuantity(match { it.items.size == 1 && it.items[0].quantity == 3 }) }
        }
    }
}
