package com.loopers.user.presentation.dto

import com.loopers.user.application.command.UserDetailResult
import com.loopers.user.domain.vo.GenderType

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
