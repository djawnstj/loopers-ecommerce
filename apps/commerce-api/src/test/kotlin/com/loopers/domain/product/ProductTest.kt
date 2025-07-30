package com.loopers.domain.product

import com.loopers.domain.product.vo.ProductStatusType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDateTime

class ProductTest {

    @Nested
    inner class `상품을 생성할 때` {

        @Test
        fun `브랜드 ID와 이름, 판매 시작 일로 상품을 생성할 수 있다`() {
            // given
            val brandId = 1L
            val name = "상품"
            val saleStartAt = LocalDateTime.parse("2025-01-01T00:00:00")

            // when
            val product = Product(brandId, name, saleStartAt)

            // then
            assertAll(
                { assertThat(product.brandId).isEqualTo(brandId) },
                { assertThat(product.name).isEqualTo(name) },
            )
        }

        @Test
        fun `브랜드 ID 와 이름, 판매 시작 일로 상품을 생성하면 상품 상태는 활성 상태이다`() {
            // given
            val brandId = 1L
            val name = "상품"
            val saleStartAt = LocalDateTime.parse("2025-01-01T00:00:00")

            // when
            val product = Product(brandId, name, saleStartAt)

            // then
            assertThat(product.status).isEqualTo(ProductStatusType.ACTIVE)
        }

        @Test
        fun `브랜드 ID, 이름, 판매 시작 일, 상태로 상품을 생성할 수 있다`() {
            // given
            val brandId = 1L
            val name = "상품"
            val saleStartAt = LocalDateTime.parse("2025-01-01T00:00:00")
            val status = ProductStatusType.INACTIVE

            // when
            val product = Product(brandId, name, saleStartAt, status)

            // then
            assertAll(
                { assertThat(product.brandId).isEqualTo(1L) },
                { assertThat(product.name).isEqualTo("상품") },
                { assertThat(product.saleStartAt).isEqualTo(LocalDateTime.parse("2025-01-01T00:00:00")) },
                { assertThat(product.status).isEqualTo(ProductStatusType.INACTIVE) },
            )

        }
    }
}
