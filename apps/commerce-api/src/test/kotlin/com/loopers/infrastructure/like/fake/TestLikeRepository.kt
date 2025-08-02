package com.loopers.infrastructure.like.fake

import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeRepository
import com.loopers.domain.like.vo.TargetType

class TestLikeRepository : LikeRepository {
    private val likes = mutableListOf<Like>()
    private var nextId = 1L

    override fun save(like: Like): Like {
        val idField = like.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(like, nextId++)

        likes.add(like)
        return like
    }

    override fun delete(like: Like) {
        this.likes.removeIf { it.userId == like.userId && it.targetId == like.targetId && it.targetId == like.targetId }
    }

    override fun existsByUserIdAndTargetIdAndTarget(userId: Long, targetId: Long, target: TargetType): Boolean =
        this.likes.any { it.userId == userId && it.targetId == targetId && it.target == target }

    fun findAll(): List<Like> = likes.toList()

    fun clear() {
        likes.clear()
        nextId = 1L
    }
}
