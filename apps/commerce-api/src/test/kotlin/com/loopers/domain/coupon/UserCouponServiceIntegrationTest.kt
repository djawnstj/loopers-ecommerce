package com.loopers.domain.coupon

import com.loopers.domain.coupon.param.GetUserCouponDetailParam
import com.loopers.fixture.coupon.CouponFixture
import com.loopers.fixture.coupon.UserCouponFixture
import com.loopers.infrastructure.coupon.JpaCouponRepository
import com.loopers.infrastructure.coupon.JpaUserCouponRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class UserCouponServiceIntegrationTest(
    private val cut: UserCouponService,
    private val userCouponRepository: JpaUserCouponRepository,
    private val couponRepository: JpaCouponRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `쿠폰을 사용하여 결제 가격을 계산할 때` {

        @Test
        fun `couponId가 null이면 계산하지 않은 금액 총 합을 반환한다`() {
            // given
            val totalAmount = BigDecimal("10000")
            val param = GetUserCouponDetailParam(1L, null, totalAmount)

            // when
            val actual = cut.calculatePayPrice(param)

            // then
            assertThat(actual).isEqualByComparingTo(BigDecimal("10000"))
        }

        @Test
        fun `사용자 쿠폰이 존재하면 쿠폰 사용 후 계산된 결제 금액을 반환한다`() {
            // given
            val userId = 1L
            val coupon = couponRepository.save(CouponFixture.`고정 할인 5000원 쿠폰`.toEntity())
            userCouponRepository.save(UserCouponFixture.기본.toEntity(coupon = coupon, userId = userId))
            val totalAmount = BigDecimal("10000")

            val param = GetUserCouponDetailParam(userId, coupon.id, totalAmount)

            // when
            val actual = cut.calculatePayPrice(param)

            // then
            assertThat(actual).isEqualByComparingTo(BigDecimal("5000"))
        }

        @Test
        fun `사용자 쿠폰이 존재하지 않으면 CoreException USER_COUPON_NOT_FOUND 예외를 던진다`() {
            // given
            val userId = 1L
            val couponId = 999L
            val totalAmount = BigDecimal("10000")
            val param = GetUserCouponDetailParam(userId, couponId, totalAmount)

            // when & then
            assertThatThrownBy {
                cut.calculatePayPrice(param)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.USER_COUPON_NOT_FOUND, "회원 식별자 1 회원이 가진 999 쿠폰을 찾을 수 없습니다.")
        }
    }
}
