package com.loopers.domain.coupon

import com.loopers.domain.coupon.param.GetUserCouponDetailParam
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

interface UserCouponService {
    fun calculatePayPrice(param: GetUserCouponDetailParam): BigDecimal
}

@Service
class UserCouponServiceImpl(
    private val userCouponRepository: UserCouponRepository,
) : UserCouponService {

    @Transactional
    override fun calculatePayPrice(param: GetUserCouponDetailParam): BigDecimal {
        if (param.couponId == null) return param.totalAmount

        val userCoupon = (
            userCouponRepository.findByUserIdAndCouponIdWithOptimisticLock(param.userId, param.couponId)
            ?: throw CoreException(
                ErrorType.USER_COUPON_NOT_FOUND,
                "회원 식별자 ${param.userId} 회원이 가진 ${param.couponId} 쿠폰을 찾을 수 없습니다.",
            )
        )

        val payPrice = userCoupon.use(param.totalAmount)
        userCouponRepository.update(userCoupon)

        return payPrice
    }
}
