package com.loopers.fixture.like

import com.loopers.domain.like.Like
import com.loopers.domain.like.vo.TargetType

sealed class LikeFixture(
    val userId: Long = 1L,
    val targetId: Long = 1L,
    val target: TargetType = TargetType.PRODUCT,
) {
    data object 기본 : LikeFixture()

    fun toEntity(): Like = Like(userId, targetId, target)
}
