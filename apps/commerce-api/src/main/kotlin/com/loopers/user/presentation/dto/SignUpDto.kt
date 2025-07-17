package com.loopers.user.presentation.dto

import com.loopers.user.application.command.UserCreateCommand
import com.loopers.user.application.command.UserCreateResult
import com.loopers.user.domain.vo.GenderType

data class SignUpRequest(
    val userId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    fun toCommand(): UserCreateCommand = UserCreateCommand(userId, email, birthDay, gender)
}

data class SignUpResponse(
    val userId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    companion object {
        operator fun invoke(result: UserCreateResult): SignUpResponse =
            SignUpResponse(result.userId, result.email, result.birthDay, result.gender)
    }
}
