package com.loopers.domain.point

interface UserPointRepository {
    fun save(userPoint: UserPoint): UserPoint
    fun findByUserId(userId: Long): UserPoint?
    fun findByUserIdWithPessimisticWrite(userId: Long): UserPoint?
}
