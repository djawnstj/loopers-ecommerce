package com.loopers.user.domain

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserService {
    fun signUp(user: User): User
    fun getUserProfile(userId: String): User
}

@Service
@Transactional(readOnly = true)
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    @Transactional
    override fun signUp(user: User): User {
        validateExistsUserId(user)

        return userRepository.save(user)
    }

    override fun getUserProfile(userId: String): User =
        (userRepository.findByUserId(UserId(userId))
            ?: throw CoreException(ErrorType.USER_NOT_FOUND, "회원 ID가 $userId 에 해당하는 유저 정보를 찾지 못했습니다."))

    private fun validateExistsUserId(user: User) {
        val userIdExisted = userRepository.existsByUserId(user.userId)

        if (userIdExisted) {
            throw CoreException(ErrorType.EXISTS_USER_LOGIN_ID)
        }
    }
}
