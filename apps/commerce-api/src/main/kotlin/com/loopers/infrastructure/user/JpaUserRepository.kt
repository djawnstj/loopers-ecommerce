package com.loopers.infrastructure.user

import com.loopers.domain.user.User
import com.loopers.domain.user.vo.LoginId
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<User, Long> {
    fun existsByLoginId(loginId: LoginId): Boolean
    fun findByLoginId(loginId: LoginId): User?
}
