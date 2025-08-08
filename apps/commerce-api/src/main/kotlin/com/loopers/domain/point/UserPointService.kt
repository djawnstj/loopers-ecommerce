package com.loopers.domain.point

import com.loopers.domain.point.vo.Point
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

interface UserPointService {
    fun createInitialPoint(userId: Long): UserPoint
    fun chargePoint(userId: Long, amount: BigDecimal): BigDecimal
    fun getUserPoint(userId: Long): UserPoint
    fun useUserPoint(userId: Long, amount: BigDecimal)
}

@Service
@Transactional(readOnly = true)
class UserPointServiceImpl(
    private val userPointRepository: UserPointRepository,
) : UserPointService {

    @Transactional
    override fun createInitialPoint(userId: Long): UserPoint = InitialUserPoint(userId).let(userPointRepository::save)

    @Transactional
    override fun chargePoint(userId: Long, amount: BigDecimal): BigDecimal {
        val userPoint = getUserPoint(userId)
        userPoint.charge(Point(amount))

        return userPoint.balance.value
    }

    override fun getUserPoint(userId: Long): UserPoint {
        val userPoint = (
                userPointRepository.findByUserId(userId)
                    ?: throw CoreException(ErrorType.USER_POINT_NOT_FOUND, "회원 식별자 $userId 에 해당하는 포인트를 찾지 못했습니다.")
                )

        return userPoint
    }

    @Transactional
    override fun useUserPoint(userId: Long, amount: BigDecimal) {
        val userPoint = (
                userPointRepository.findByUserIdWithPessimisticWrite(userId)
                    ?: throw CoreException(ErrorType.USER_POINT_NOT_FOUND, "회원 식별자 $userId 에 해당하는 포인트를 찾지 못했습니다.")
                )

        userPoint.deduct(Point(amount))
    }

    private fun InitialUserPoint(userId: Long): UserPoint =
        UserPoint(userId, Point(BigDecimal.ZERO))
}
