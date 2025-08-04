package com.loopers.infrastructure.point

import com.loopers.domain.point.UserPoint
import com.loopers.domain.point.UserPointRepository
import org.springframework.stereotype.Component

@Component
class UserPointRepositoryImpl(
    private val jpaUserPointRepository: JpaUserPointRepository,
) : UserPointRepository {
    override fun save(userPoint: UserPoint): UserPoint = jpaUserPointRepository.save(userPoint)

    override fun findByUserId(userId: Long): UserPoint? = jpaUserPointRepository.findByUserId(userId)
}
