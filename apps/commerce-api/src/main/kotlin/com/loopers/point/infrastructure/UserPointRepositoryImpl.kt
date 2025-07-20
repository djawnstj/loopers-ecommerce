package com.loopers.point.infrastructure

import com.loopers.point.domain.UserPoint
import com.loopers.point.domain.UserPointRepository
import org.springframework.stereotype.Component

@Component
class UserPointRepositoryImpl(
    private val jpaUserPointRepository: JpaUserPointRepository,
) : UserPointRepository {
    override fun save(userPoint: UserPoint): UserPoint = jpaUserPointRepository.save(userPoint)

    override fun findByUserId(userId: Long): UserPoint? = jpaUserPointRepository.findByUserId(userId)
}
