package com.loopers.application.like.command

import com.loopers.domain.like.vo.TargetType

data class DeleteLikeCommand(
    val loginId: String,
    val targetId: Long,
    val target: TargetType,
)
