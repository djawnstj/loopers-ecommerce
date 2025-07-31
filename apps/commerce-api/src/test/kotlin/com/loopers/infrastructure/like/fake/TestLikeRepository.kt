package com.loopers.infrastructure.like.fake

import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeRepository

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

    fun findAll(): List<Like> = likes.toList()

    fun clear() {
        likes.clear()
        nextId = 1L
    }
}