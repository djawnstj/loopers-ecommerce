package com.loopers.domain.point.fake

import com.loopers.domain.point.UserPoint
import com.loopers.domain.point.UserPointService
import com.loopers.domain.point.vo.Point
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import java.math.BigDecimal

class TestUserPointService : UserPointService {
    private val userPoints = mutableMapOf<Long, UserPoint>()
    private var nextId = 1L

    override fun createInitialPoint(userId: Long): UserPoint {
        val userPoint = UserPoint(userId, Point(BigDecimal.ZERO))

        // Reflection을 사용하여 ID 설정
        val idField = userPoint.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(userPoint, nextId++)

        userPoints[userId] = userPoint
        return userPoint
    }

    override fun chargePoint(userId: Long, amount: BigDecimal): BigDecimal {
        val userPoint = getUserPoint(userId)
        userPoint.charge(Point(amount))
        return userPoint.balance.value
    }

    override fun getUserPoint(userId: Long): UserPoint {
        return userPoints[userId]
            ?: throw CoreException(ErrorType.USER_POINT_NOT_FOUND, "회원 식별자 $userId 에 해당하는 포인트를 찾지 못했습니다.")
    }

    override fun useUserPoint(userId: Long, amount: BigDecimal) {
        val userPoint = getUserPoint(userId)
        userPoint.deduct(Point(amount))
    }

    fun addUserPoint(userPoint: UserPoint) {
        userPoints[userPoint.userId] = userPoint
    }

    fun clear() {
        userPoints.clear()
        nextId = 1L
    }
}
