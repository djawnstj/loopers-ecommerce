package com.loopers.infrastructure.like

import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeRepository
import com.loopers.domain.like.vo.TargetType
import org.springframework.stereotype.Component

@Component
class LikeRepositoryImpl(
    private val jpaLikeRepository: JpaLikeRepository,
) : LikeRepository {
    override fun save(like: Like): Like = jpaLikeRepository.save(like)

    override fun delete(like: Like) = jpaLikeRepository.deleteByUserIdAndTargetIdAndTarget(
        like.userId,
        like.targetId,
        like.target,
    )

    override fun existsByUserIdAndTargetIdAndTarget(userId: Long, targetId: Long, target: TargetType): Boolean =
        jpaLikeRepository.existsByUserIdAndTargetIdAndTarget(userId, targetId, target)
}
