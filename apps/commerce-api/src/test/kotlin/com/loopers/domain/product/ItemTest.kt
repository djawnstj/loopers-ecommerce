package com.loopers.domain.product

import com.loopers.fixture.product.ProductFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ItemTest {

    @Nested
    inner class `상품 항목을 생성할 때` {

        @Test
        fun `상품, 가격, 수량으로 상품 항목을 생성할 수 있다`() {
            // given
            val product = ProductFixture.기본.toEntity()
            val name = "아이템 이름"
            val price = BigDecimal("10000")
            val quantity = 100

            // when
            val productItem = ProductItem(product, name, price, quantity)

            // then
            assertThat(productItem).extracting("name", "price", "quantity").containsExactly("아이템 이름", BigDecimal("10000"), 100)
        }
    }
}
