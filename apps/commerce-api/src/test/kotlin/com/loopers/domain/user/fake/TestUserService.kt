package com.loopers.domain.user.fake

import com.loopers.domain.user.User
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

class TestUserService : UserService {
    private val users = mutableListOf<User>()
    private var nextId = 1L

    override fun signUp(user: User): User {
        val idField = user.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(user, nextId++)

        users.add(user)
        return user
    }

    override fun getUserProfile(loginId: String): User {
        return users.find { it.loginId.value == loginId }
            ?: throw CoreException(ErrorType.USER_NOT_FOUND, "로그인 ID가 $loginId 에 해당하는 유저 정보를 찾지 못했습니다.")
    }

    fun addUsers(users: List<User>) {
        users.forEach { signUp(it) }
    }

    fun clear() {
        users.clear()
        nextId = 1L
    }
}
