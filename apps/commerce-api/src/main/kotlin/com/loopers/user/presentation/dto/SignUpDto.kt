package com.loopers.user.presentation.dto

import com.loopers.user.application.command.UserCreateCommand
import com.loopers.user.application.command.UserCreateResult
import com.loopers.user.domain.vo.GenderType

data class SignUpRequest(
    val loginId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    fun toCommand(): UserCreateCommand = UserCreateCommand(loginId, email, birthDay, gender)
}

data class SignUpResponse(
    val loginId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    companion object {
        operator fun invoke(result: UserCreateResult): SignUpResponse =
            SignUpResponse(result.loginId, result.email, result.birthDay, result.gender)
    }
}
