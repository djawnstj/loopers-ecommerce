package com.loopers.infrastructure.like

import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeRepository
import org.springframework.stereotype.Component

@Component
class LikeRepositoryImpl(
    private val jpaLikeRepository: JpaLikeRepository,
) : LikeRepository {
    override fun save(like: Like): Like = jpaLikeRepository.save(like)
}
