package com.loopers.user.application

import com.loopers.user.application.command.UserSignUpCommand
import com.loopers.user.application.command.UserSignUpResult
import com.loopers.user.domain.User
import com.loopers.user.domain.UserRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserService {
    fun signUp(user: UserSignUpCommand): UserSignUpResult
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

    private fun validateExistsUserId(user: User) {
        val userIdExisted = userRepository.existsByUserId(user.userId)

        if (userIdExisted) {
            throw CoreException(ErrorType.EXISTS_USER_LOGIN_ID)
        }
    }
}
