package com.loopers.domain.like

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface LikeService {
    fun addLike(like: Like)
}

@Service
@Transactional(readOnly = true)
class LikeServiceImpl(
    private val likeRepository: LikeRepository,
) : LikeService {

    @Transactional
    override fun addLike(like: Like) {
        likeRepository.save(like)
    }
}
