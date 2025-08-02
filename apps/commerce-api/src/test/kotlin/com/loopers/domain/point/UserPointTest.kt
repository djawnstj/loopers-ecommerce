package com.loopers.domain.point

import com.loopers.domain.point.vo.Point
import com.loopers.fixture.point.UserPointFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import java.math.BigDecimal

class UserPointTest {

    @Test
    fun `포인트 객체를 생성할 수 있다`() {
        // given
        val anyUserPoint = UserPointFixture.기본

        // when
        val actual = UserPoint(anyUserPoint.userId, Point(anyUserPoint.balance))

        // then
        assertAll(
            { assertThat(actual.userId).isEqualTo(1L) },
            { assertThat(actual.balance).isEqualTo(Point(BigDecimal(0))) },
        )
    }

    @Nested
    inner class `포인트를 충전할 때` {
        @Test
        fun `0 이하의 포인트를 충전하면 CoreException REQUIRED_POSITIVE_POINT_CHARGE_AMOUNT 예외를 던진다`() {
            // given
            val cut = UserPointFixture.`0 포인트`.toEntity()
            val amount = Point(UserPointFixture.`0 포인트`.balance)

            // when then
            assertThatThrownBy {
                cut.charge(amount)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.REQUIRED_POSITIVE_POINT_CHARGE_AMOUNT, "포인트 충전은 0 보다 커야합니다.")
        }

        @Test
        fun `0보다 큰 포인트를 충전할 수 있다`() {
            // given
            val cut = UserPointFixture.`0 포인트`.toEntity()
            val amount = Point(UserPointFixture.`양수 포인트`.balance)

            // when then
            assertDoesNotThrow {
                cut.charge(amount)
            }
        }

        @Test
        fun `충전 후 잔액은 충전 포인트가 합해진 포인트가 된다`() {
            // given
            val cut = UserPointFixture.`0 포인트`.toEntity()
            val amount = Point(UserPointFixture.`양수 포인트`.balance)

            // when
            cut.charge(amount)

            // then
            val actual = cut.balance
            assertThat(actual).isEqualTo(Point(BigDecimal(0.1)))
        }
    }

    @Nested
    inner class `포인트를 차감할 때` {
        @Test
        fun `잔액보다 많은 포인트를 차감하면 CoreException POINT_BALANCE_EXCEEDED 예외를 던진다`() {
            // given
            val cut = UserPointFixture.`500 포인트`.toEntity()
            val amount = Point(UserPointFixture.`1000 포인트`.balance)

            // when then
            assertThatThrownBy {
                cut.deduct(amount)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType")
                .isEqualTo(ErrorType.POINT_BALANCE_EXCEEDED)
        }

        @Test
        fun `잔액과 같은 포인트를 차감할 수 있다`() {
            // given
            val cut = UserPointFixture.`1000 포인트`.toEntity()
            val amount = Point(UserPointFixture.`1000 포인트`.balance)

            // when then
            assertDoesNotThrow {
                cut.deduct(amount)
            }
        }

        @Test
        fun `잔액보다 적은 포인트를 차감할 수 있다`() {
            // given
            val cut = UserPointFixture.`1000 포인트`.toEntity()
            val amount = Point(BigDecimal("999.9"))

            // when then
            assertDoesNotThrow {
                cut.deduct(amount)
            }
        }

        @Test
        fun `차감 후 잔액은 기존 잔액에서 차감 포인트를 뺀 포인트가 된다`() {
            // given
            val cut = UserPointFixture.`1000 포인트`.toEntity()
            val amount = Point(UserPointFixture.`500 포인트`.balance)

            // when
            cut.deduct(amount)

            // then
            val actual = cut.balance
            assertThat(actual.value).isEqualByComparingTo(BigDecimal("500"))
        }
    }
}
