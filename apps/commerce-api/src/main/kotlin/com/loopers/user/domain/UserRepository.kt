package com.loopers.user.domain

import com.loopers.user.domain.vo.LoginId

interface UserRepository {
    fun existsByLoginId(loginId: LoginId): Boolean
    fun save(user: User): User
    fun findByLoginId(loginId: LoginId): User?
}
