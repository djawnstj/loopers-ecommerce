package com.loopers.user.presentation.dto

import com.loopers.user.application.command.UserCreateCommand
import com.loopers.user.application.command.UserCreateResult
import com.loopers.user.domain.vo.GenderType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SignUpRequest(
    @field:Pattern(regexp = "^[a-zA-Z0-9]{6,10}$", message = "로그인 ID는 6자 이상 10자 이내의 영문 및 숫자만 허용됩니다.")
    val loginId: String,

    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    val email: String,

    @field:NotBlank(message = "생년월일은 필수입니다.")
    @field:Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일 형식은 'yyyy-MM-dd' 형식이어야 합니다.")
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
