package com.loopers.user.application

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.user.application.command.UserDetailResult
import com.loopers.user.application.command.UserSignUpCommand
import com.loopers.user.application.command.UserSignUpResult
import com.loopers.user.domain.User
import com.loopers.user.domain.UserRepository
import com.loopers.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserService {
    fun signUp(command: UserSignUpCommand): UserSignUpResult
    fun searchDetailByUserId(userId: String): UserDetailResult
}

@Service
@Transactional(readOnly = true)
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    @Transactional
    override fun signUp(command: UserSignUpCommand): UserSignUpResult {
        val user = command.toEntity()

        validateExistsUserId(user)

        userRepository.save(user)

        return UserSignUpResult.fromEntity(user)
    }

    override fun searchDetailByUserId(userId: String): UserDetailResult =
        (userRepository.findByUserId(UserId(userId))
            ?: throw CoreException(ErrorType.USER_NOT_FOUND, "회원 ID가 $userId 에 해당하는 유저 정보를 찾지 못했습니다."))
            .let(UserDetailResult::invoke)

    private fun validateExistsUserId(user: User) {
        val userIdExisted = userRepository.existsByUserId(user.userId)

        if (userIdExisted) {
            throw CoreException(ErrorType.EXISTS_USER_LOGIN_ID)
        }
    }
}
