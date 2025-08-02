package com.loopers.domain.product.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PriceTest {

    @Nested
    inner class `Price 값객체를 생성할 때` {

        @Test
        fun `음수 값으로 생성하면 예외를 던진다`() {
            // given
            val value = BigDecimal("-0.01")

            // when then
            assertThatThrownBy { Price(value) }
                .isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.INVALID_PRICE_VALUE, "가격은 0 이상이어야 합니다.")
        }

        @Test
        fun `0 이상의 값으로 생성하면 Price 객체를 생성할 수 있다`() {
            // given
            val value = BigDecimal("0")

            // when
            val actual = Price(value)

            // then
            assertThat(actual.value).isEqualTo(BigDecimal("0"))
        }
    }
}
