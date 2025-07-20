package com.loopers.user.infrastructure

import com.loopers.user.domain.User
import com.loopers.user.domain.vo.LoginId
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<User, Long> {
    fun existsByLoginId(loginId: LoginId): Boolean
    fun findByLoginId(loginId: LoginId): User?
}
