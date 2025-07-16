package com.loopers.user.fake

import com.loopers.user.domain.User
import com.loopers.user.domain.UserRepository
import com.loopers.user.domain.vo.UserId

class TestUserRepository : UserRepository {
    private val users = mutableMapOf<Long, User>()

    override fun existsByUserId(userId: UserId): Boolean = users.values.any { it.userId == userId }

    override fun save(user: User): User {
        val id = users.keys.size.toLong() + 1
        users[id] = user

        return user
    }
}
