package com.loopers.domain.order.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MoneyTest {

    @Nested
    inner class `Amount 값객체를 생성할 때` {

        @Test
        fun `음수 값으로 생성하면 CoreException INVALID_MONEY_VALUE 예외를 던진다`() {
            // given
            val value = BigDecimal("-0.01")

            // when then
            assertThatThrownBy { Money(value) }
                .isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.INVALID_MONEY_VALUE, "가격은 0 이상이어야 합니다.")
        }

        @Test
        fun `0 이상의 값으로 생성하면 Money 객체를 생성할 수 있다`() {
            // given
            val value = BigDecimal("0")

            // when
            val actual = Money(value)

            // then
            assertThat(actual.value).isEqualTo(BigDecimal("0"))
        }
    }
}
