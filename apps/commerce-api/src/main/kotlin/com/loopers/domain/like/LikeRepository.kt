package com.loopers.domain.like

import com.loopers.domain.like.vo.TargetType

interface LikeRepository {
    fun save(like: Like): Like
    fun delete(like: Like)
    fun existsByUserIdAndTargetIdAndTarget(userId: Long, targetId: Long, target: TargetType): Boolean
}
