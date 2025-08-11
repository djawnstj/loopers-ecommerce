package com.loopers.domain.coupon

import com.loopers.domain.coupon.vo.CouponType
import com.loopers.fixture.coupon.CouponFixture
import com.loopers.fixture.coupon.UserCouponFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId

class UserCouponTest {

    @Nested
    inner class `쿠폰을 사용할 때` {

        @Test
        fun `이미 사용된 쿠폰을 다시 사용하려고 하면 CoreException ALREADY_USED_USER_COUPON 예외를 던진다`() {
            // given
            val coupon = CouponFixture.`고정 할인 5000원 쿠폰`.toEntity()
            val userCoupon = UserCouponFixture.기본.toEntity(
                coupon = coupon,
                usedAt = LocalDateTime.now(),
            )
            val orderAmount = BigDecimal("10000")

            // when & then
            assertThatThrownBy {
                userCoupon.use(orderAmount)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType")
                .isEqualTo(ErrorType.ALREADY_USED_USER_COUPON)
        }

        @Test
        fun `쿠폰의 사용 시간을 기록한다`() {
            // given
            val fixedTime = LocalDateTime.parse("2025-01-01T00:00:00")
            val clock = Clock.fixed(fixedTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault())
            val coupon = Coupon(
                name = "5000원 할인 쿠폰",
                quantity = 100L,
                discount = BigDecimal("5000"),
                type = CouponType.FIXED,
            )
            val userCoupon = UserCouponFixture.기본.toEntity(coupon = coupon)
            val orderAmount = BigDecimal("10000")

            // when
            userCoupon.use(orderAmount, clock)

            // then
            assertThat(userCoupon.usedAt).isEqualTo(LocalDateTime.parse("2025-01-01T00:00:00"))
        }

        @Test
        fun `할인이 적용된 금액을 반환한다`() {
            // given
            val coupon = Coupon(
                name = "10% 할인 쿠폰",
                quantity = 100L,
                discount = BigDecimal("10"),
                type = CouponType.PERCENT,
            )
            val userCoupon = UserCouponFixture.기본.toEntity(coupon = coupon)
            val orderAmount = BigDecimal("10000")

            // when
            val actual = userCoupon.use(orderAmount)

            // then
            assertThat(actual).isEqualByComparingTo(BigDecimal("9000"))
        }
    }
}
