package com.loopers.domain.like

interface LikeRepository {
    fun save(like: Like): Like
}
