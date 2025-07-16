package com.loopers.user.infrastructure

import com.loopers.user.domain.User
import com.loopers.user.domain.vo.UserId
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<User, Long> {
    fun existsByUserId(userId: UserId): Boolean
    fun findByUserId(userId: UserId): User?
}
