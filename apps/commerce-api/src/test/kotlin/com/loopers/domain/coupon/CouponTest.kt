package com.loopers.domain.coupon

import com.loopers.domain.coupon.vo.CouponType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CouponTest {

    @Nested
    inner class `할인 금액을 계산할 때` {

        @Test
        fun `고정 할인 쿠폰으로 주문 금액에서 할인 금액을 차감한 금액을 반환한다`() {
            // given
            val coupon = Coupon(
                name = "5000원 할인 쿠폰",
                quantity = 100L,
                discount = BigDecimal("5000"),
                type = CouponType.FIXED,
            )
            val orderAmount = BigDecimal("10000")

            // when
            val actual = coupon.calculateDiscount(orderAmount)

            // then
            assertThat(actual).isEqualByComparingTo(BigDecimal("5000"))
        }

        @Test
        fun `고정 할인 쿠폰의 할인 금액이 주문 금액보다 크거나 같은 경우 0을 반환한다`() {
            // given
            val coupon = Coupon(
                name = "10000원 할인 쿠폰",
                quantity = 100L,
                discount = BigDecimal("5001"),
                type = CouponType.FIXED,
            )
            val orderAmount = BigDecimal("5000")

            // when
            val actual = coupon.calculateDiscount(orderAmount)

            // then
            assertThat(actual).isEqualByComparingTo(BigDecimal.ZERO)
        }

        @Test
        fun `퍼센트 할인 쿠폰으로 주문 금액에서 비율만큼 할인된 금액을 반환한다`() {
            // given
            val coupon = Coupon(
                name = "10% 할인 쿠폰",
                quantity = 100L,
                discount = BigDecimal("10"),
                type = CouponType.PERCENT,
            )
            val orderAmount = BigDecimal("10000")

            // when
            val actual = coupon.calculateDiscount(orderAmount)

            // then
            assertThat(actual).isEqualByComparingTo(BigDecimal("9000"))
        }
    }
}
