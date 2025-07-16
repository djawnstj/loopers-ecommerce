package com.loopers.user.presentation.dto

import com.loopers.user.application.command.UserSignUpCommand
import com.loopers.user.application.command.UserSignUpResult
import com.loopers.user.domain.vo.GenderType

data class SignUpRequest(
    val userId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    fun toCommand(): UserSignUpCommand = UserSignUpCommand(userId, email, birthDay, gender)
}

data class SignUpResponse(
    val userId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    companion object {
        operator fun invoke(result: UserSignUpResult): SignUpResponse =
            SignUpResponse(result.userId, result.email, result.birthDay, result.gender)
    }
}
