package com.loopers.domain.point.vo

import com.loopers.fixture.point.UserPointFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal

class PointTest {
    @Nested
    inner class `포인트 값객체를 생성할 때` {
        @Test
        fun `포인트가 0 보다 작으면 CoreException REQUIRED_ZERO_OR_POSITIVE_POINT 예외를 던진다`() {
            // given
            val negativePoint = UserPointFixture.`음수 포인트`.balance

            // when then
            assertThatThrownBy {
                Point(negativePoint)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.REQUIRED_ZERO_OR_POSITIVE_POINT, "포인트는 0 이상이어야 합니다.")
        }

        @Test
        fun `포인트가 0 이상이면 객체를 생성할 수 있다`() {
            // given
            val zeroPoint = UserPointFixture.`0 포인트`.balance

            // when then
            assertDoesNotThrow { Point(zeroPoint) }
        }

        @Test
        fun `객체를 생성하면 포인트 값을 갖고 있다`() {
            // given
            val anyPoint = UserPointFixture.기본.balance

            // when
            val actual = Point(anyPoint)

            // then
            assertThat(actual.value).isEqualTo(BigDecimal(0))
        }
    }

    @Nested
    inner class `포인트를 더하기 연산할 때` {
        @Test
        fun `기존 포인트에 새로운 객체를 생성 후 반환 한다`() {
            // given
            val anyPoint = Point(UserPointFixture.기본.balance)
            val cut = Point(UserPointFixture.`0 포인트`.balance)

            // when
            val actual = cut + anyPoint

            // then
            assertThat(actual).isNotSameAs(cut)
        }

        @Test
        fun `반환 된 새로운 객체는 두 포인트를 더한 값을 갖고있다`() {
            // given
            val anyPoint = Point(UserPointFixture.`1 포인트`.balance)
            val cut = Point(UserPointFixture.`1 포인트`.balance)

            // when
            val actual = cut + anyPoint

            // then
            assertThat(actual).isEqualTo(Point(BigDecimal(2)))
        }
    }
}
