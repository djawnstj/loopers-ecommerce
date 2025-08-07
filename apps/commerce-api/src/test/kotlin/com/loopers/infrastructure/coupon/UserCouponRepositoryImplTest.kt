package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.UserCouponRepository
import com.loopers.fixture.coupon.CouponFixture
import com.loopers.fixture.coupon.UserCouponFixture
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserCouponRepositoryImplTest(
    private val cut: UserCouponRepository,
    private val jpaCouponRepository: JpaCouponRepository,
    private val jpaUserCouponRepository: JpaUserCouponRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `낙관적 락으로 사용자 쿠폰을 조회할 때` {

        @Test
        fun `존재하지 않는 사용자 ID로 조회하면 null을 반환한다`() {
            // given
            val coupon = jpaCouponRepository.saveAndFlush(CouponFixture.`고정 할인 5000원 쿠폰`.toEntity())
            val nonExistentUserId = 999L

            // when
            val actual = cut.findByUserIdAndCouponIdWithOptimisticLock(nonExistentUserId, coupon.id)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `존재하지 않는 쿠폰 ID로 조회하면 null을 반환한다`() {
            // given
            val coupon = jpaCouponRepository.saveAndFlush(CouponFixture.`고정 할인 5000원 쿠폰`.toEntity())
            val userCoupon = jpaUserCouponRepository.saveAndFlush(UserCouponFixture.기본.toEntity(coupon))
            val nonExistentCouponId = 999L

            // when
            val actual = cut.findByUserIdAndCouponIdWithOptimisticLock(userCoupon.userId, nonExistentCouponId)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `삭제된 사용자 쿠폰은 조회되지 않는다`() {
            // given
            val coupon = jpaCouponRepository.saveAndFlush(CouponFixture.`고정 할인 5000원 쿠폰`.toEntity())
            val userCoupon = jpaUserCouponRepository.saveAndFlush(UserCouponFixture.기본.toEntity(coupon))

            // 쿠폰 삭제
            userCoupon.delete()
            jpaUserCouponRepository.saveAndFlush(userCoupon)

            // when
            val actual = cut.findByUserIdAndCouponIdWithOptimisticLock(userCoupon.userId, coupon.id)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `존재하는 사용자 ID와 쿠폰 ID로 조회하면 UserCoupon을 반환한다`() {
            // given
            val coupon = jpaCouponRepository.saveAndFlush(CouponFixture.`고정 할인 5000원 쿠폰`.toEntity())
            val userCoupon = jpaUserCouponRepository.saveAndFlush(UserCouponFixture.기본.toEntity(coupon))

            // when
            val actual = cut.findByUserIdAndCouponIdWithOptimisticLock(userCoupon.userId, coupon.id)

            // then
            assertThat(actual).isNotNull
                .extracting(
                    "userId",
                    "coupon.id",
                    "coupon.name",
                ).containsExactly(
                    userCoupon.userId,
                    coupon.id,
                    "5000원 할인 쿠폰",
                )
        }
    }
}
