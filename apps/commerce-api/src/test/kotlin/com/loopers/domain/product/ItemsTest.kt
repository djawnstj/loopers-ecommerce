package com.loopers.domain.product

import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ItemsTest {

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
            val item1 = ProductItemFixture.`검은색 라지 만원`.toEntity(product)
            val item2 = ProductItemFixture.`빨간색 라지 만원`.toEntity(product)

            val items = mutableListOf(item1, item2)

            // when
            val productItems = ProductItems(items)

            // then
            assertThat(productItems).hasSize(2)
                .extracting("name", "price", "quantity")
                .containsExactly(
                    tuple("검은색 라지", BigDecimal("10000"), 10),
                    tuple("빨간색 라지", BigDecimal("10000"), 10),
                )
        }
    }

    @Nested
    inner class `상품 항목을 추가할 때` {

        @Test
        fun `상품 항목을 추가할 수 있다`() {
            // given
            val productItems = ProductItems()
            val product = ProductFixture.기본.toEntity()
            val item = ProductItemFixture.`검은색 라지 만원`.toEntity(product)

            // when
            productItems.addItem(item)

            // then
            assertThat(productItems).hasSize(1)
                .extracting("name", "price", "quantity")
                .containsExactly(
                    tuple("검은색 라지", BigDecimal("10000"), 10),
                )
        }
    }
}
