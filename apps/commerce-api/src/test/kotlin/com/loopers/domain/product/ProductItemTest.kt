package com.loopers.domain.product

import com.loopers.domain.product.vo.Quantity
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.product.ProductItemFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal

class ProductItemTest {

    @Test
    fun `상품 아이템 객체를 생성할 수 있다`() {
        // given
        val product = ProductFixture.`활성 상품 1`.toEntity()
        val name = "검은색 라지"
        val price = BigDecimal("10000")
        val quantity = 10

        // when
        val actual = ProductItem(product, name, price, quantity)

        // then
        assertThat(actual.product).isEqualTo(product)
        assertThat(actual.name).isEqualTo("검은색 라지")
        assertThat(actual.price.value).isEqualByComparingTo(BigDecimal("10000"))
        assertThat(actual.quantity.value).isEqualTo(10)
    }

    @Nested
    inner class `수량을 차감할 때` {

        @Test
        fun `음수를 차감하면 CoreException INSUFFICIENT_PRODUCT_QUANTITY 예외를 던진다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val cut = ProductItemFixture.`검은색 라지 만원`.toEntity(product) // quantity = 10
            val deductAmount = -5

            // when then
            assertThatThrownBy {
                cut.deduct(deductAmount)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.INSUFFICIENT_PRODUCT_QUANTITY, "상품 재고가 부족합니다.")
        }

        @Test
        fun `재고보다 많은 수량을 차감하면 CoreException INSUFFICIENT_PRODUCT_QUANTITY 예외를 던진다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val cut = ProductItemFixture.`검은색 라지 만원`.toEntity(product) // quantity = 10
            val deductAmount = 15

            // when then
            assertThatThrownBy {
                cut.deduct(deductAmount)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.INSUFFICIENT_PRODUCT_QUANTITY, "상품 재고가 부족합니다.")
        }

        @Test
        fun `재고와 같은 수량을 차감할 수 있다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val cut = ProductItemFixture.`검은색 라지 만원`.toEntity(product) // quantity = 10
            val deductAmount = 10

            // when then
            assertDoesNotThrow {
                cut.deduct(deductAmount)
            }
        }

        @Test
        fun `재고보다 적은 수량을 차감할 수 있다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val cut = ProductItemFixture.`검은색 라지 만원`.toEntity(product) // quantity = 10
            val deductAmount = 7

            // when
            cut.deduct(deductAmount)

            // then
            assertThat(cut.quantity).isEqualTo(Quantity(3))
        }

        @Test
        fun `0을 차감할 수 있다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val cut = ProductItemFixture.`검은색 라지 만원`.toEntity(product) // quantity = 10
            val deductAmount = 0

            // when then
            assertDoesNotThrow {
                cut.deduct(deductAmount)
            }
        }

        @Test
        fun `차감 후 수량이 정확히 감소한다`() {
            // given
            val product = ProductFixture.`활성 상품 1`.toEntity()
            val cut = ProductItemFixture.`검은색 라지 만원`.toEntity(product) // quantity = 10
            val deductAmount = 3

            // when
            cut.deduct(deductAmount)

            // then
            assertThat(cut.quantity).isEqualTo(Quantity(7))
        }
    }
}
