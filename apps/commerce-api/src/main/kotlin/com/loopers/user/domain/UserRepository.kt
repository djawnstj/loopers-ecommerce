package com.loopers.user.domain

import com.loopers.user.domain.vo.UserId

interface UserRepository {
    fun existsByUserId(userId: UserId): Boolean
    fun save(user: User): User
    fun findByUserId(userId: UserId): User?
}
