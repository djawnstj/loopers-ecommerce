package com.loopers.point.domain

interface UserPointRepository {
    fun save(userPoint: UserPoint): UserPoint
    fun findByUserId(userId: Long): UserPoint?
}
