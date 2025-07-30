package com.loopers.domain.product

import com.loopers.domain.product.vo.Price
import com.loopers.domain.product.vo.ProductStatusType
import com.loopers.domain.product.vo.Quantity
import com.loopers.fixture.product.ProductFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.math.BigDecimal

class ProductItemTest {

    @Nested
    inner class `상품 항목을 생성할 때` {

        @Test
        fun `상품, 가격, 수량으로 상품 항목을 생성할 수 있다`() {
            // given
            val product = ProductFixture.기본.toEntity()
            val price = BigDecimal("10000")
            val quantity = 100

            // when
            val productItem = ProductItem(product, price, quantity)

            // then
            assertAll(
                { assertThat(productItem.product).extracting("brandId", "name", "status").containsExactly(1L, "상품", ProductStatusType.ACTIVE) },
                { assertThat(productItem.price).isEqualTo(Price(BigDecimal("10000"))) },
                { assertThat(productItem.quantity).isEqualTo(Quantity(100)) },
            )
        }
    }
}
