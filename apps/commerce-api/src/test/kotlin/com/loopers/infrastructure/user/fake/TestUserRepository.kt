package com.loopers.infrastructure.user.fake

import com.loopers.domain.user.User
import com.loopers.domain.user.UserRepository
import com.loopers.domain.user.vo.LoginId

class TestUserRepository : UserRepository {
    private val users = mutableMapOf<Long, User>()

    override fun existsByLoginId(loginId: LoginId): Boolean = users.values.any { it.loginId == loginId }

    override fun save(user: User): User {
        val id = users.keys.size.toLong() + 1
        users[id] = user

        return user
    }

    override fun findByLoginId(loginId: LoginId): User? = users.values.find {
        it.loginId == loginId
    }
}
