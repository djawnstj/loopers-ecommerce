package com.loopers.domain.user

import com.loopers.domain.user.vo.LoginId
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserService {
    fun signUp(user: User): User
    fun getUserProfile(loginId: String): User
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

    override fun getUserProfile(loginId: String): User =
        (
            userRepository.findByLoginId(LoginId(loginId))
            ?: throw CoreException(ErrorType.USER_NOT_FOUND, "로그인 ID가 $loginId 에 해당하는 유저 정보를 찾지 못했습니다.")
        )

    private fun validateExistsUserId(user: User) {
        val loginIdExisted = userRepository.existsByLoginId(user.loginId)

        if (loginIdExisted) {
            throw CoreException(ErrorType.EXISTS_USER_LOGIN_ID)
        }
    }
}
