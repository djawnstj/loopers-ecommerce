package com.loopers.domain.product

import com.loopers.fixture.product.ProductFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.math.BigDecimal

class ProductItemsTest {

    @Nested
    inner class `상품 항목 컬렉션을 생성할 때` {

        @Test
        fun `빈 상품 항목 컬렉션을 생성할 수 있다`() {
            // when
            val productItems = ProductItems()

            // then
            assertThat(productItems).hasSize(0)
        }

        @Test
        fun `기존 상품 항목 리스트로 컬렉션을 생성할 수 있다`() {
            // given
            val product = ProductFixture.기본.toEntity()
            val item1 = ProductItem(product, BigDecimal("10000"), 100)
            val item2 = ProductItem(product, BigDecimal("20000"), 50)
            val items = mutableListOf(item1, item2)

            // when
            val productItems = ProductItems(items)

            // then
            assertThat(productItems).hasSize(2)
        }
    }

    @Nested
    inner class `상품 항목을 추가할 때` {

        @Test
        fun `상품 항목을 추가할 수 있다`() {
            // given
            val productItems = ProductItems()
            val product = ProductFixture.기본.toEntity()
            val item = ProductItem(product, BigDecimal("10000"), 100)

            // when
            productItems.addItem(item)

            // then
            assertAll(
                { assertThat(productItems).hasSize(1) },
                { assertThat(productItems).extracting("price", "quantity").containsExactly(tuple(BigDecimal("10000"), 100)) },
            )
        }
    }
}
