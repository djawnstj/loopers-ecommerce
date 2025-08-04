package com.loopers.domain.like

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface LikeService {
    fun addLike(like: Like)
    fun cancelLike(like: Like)
}

@Service
@Transactional(readOnly = true)
class LikeServiceImpl(
    private val likeRepository: LikeRepository,
) : LikeService {

    @Transactional
    override fun addLike(like: Like) {
        val exists =
            likeRepository.existsByUserIdAndTargetIdAndTarget(like.userId, like.targetId, like.target)

        if (exists) return

        likeRepository.save(like)
    }

    @Transactional
    override fun cancelLike(like: Like) {
        likeRepository.delete(like)
    }
}
