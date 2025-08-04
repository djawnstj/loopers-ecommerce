package com.loopers.application.like.command

import com.loopers.domain.like.vo.TargetType

data class CreateProductLikeCommand(
    val loginId: String,
    val targetId: Long,
    val target: TargetType = TargetType.PRODUCT,
)
