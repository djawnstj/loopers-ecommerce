package com.loopers.infrastructure.point.fake

import com.loopers.domain.point.UserPoint
import com.loopers.domain.point.UserPointRepository

class TestUserPointRepository : UserPointRepository {
    private val points: MutableMap<Long, UserPoint> = mutableMapOf()

    override fun save(userPoint: UserPoint): UserPoint {
        val id = points.keys.size.toLong() + 1
        points[id] = userPoint

        return userPoint
    }

    override fun findByUserId(userId: Long): UserPoint? = points.values.find { it.userId == userId }
}
