package com.loopers.domain.product.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class QuantityTest {

    @Nested
    inner class `Quantity 값객체를 생성할 때` {
        @Test
        fun `음수 값으로 생성하면 예외를 던진다`() {
            // given
            val value = -1

            // when then
            assertThatThrownBy { Quantity(value) }
                .isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.INVALID_QUANTITY_VALUE, "수량은 0 이상이어야 합니다.")
        }

        @Test
        fun `0 이상의 값으로 생성하면 Quantity 객체를 생성할 수 있다`() {
            // given
            val value = 0

            // when
            val actual = Quantity(value)

            // then
            assertThat(actual.value).isEqualTo(0)
        }
    }
}
