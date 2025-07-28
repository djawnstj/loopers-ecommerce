package com.loopers.presentation.user.dto

import com.loopers.application.user.command.UserDetailResult
import com.loopers.domain.user.vo.GenderType

data class MyDetailResponse(
    val loginId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    companion object {
        operator fun invoke(result: UserDetailResult): MyDetailResponse =
            MyDetailResponse(result.loginId, result.email, result.birthDay, result.gender)
    }
}
