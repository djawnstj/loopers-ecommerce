package com.loopers.point.fake

import com.loopers.point.domain.UserPoint
import com.loopers.point.domain.UserPointRepository

class TestUserPointRepository : UserPointRepository {
    private val points: MutableMap<Long, UserPoint> = mutableMapOf()

    override fun save(userPoint: UserPoint): UserPoint {
        val id = points.keys.size.toLong() + 1
        points[id] = userPoint

        return userPoint
    }

    override fun findByUserId(userId: Long): UserPoint? = points.values.find { it.userId == userId }
}
