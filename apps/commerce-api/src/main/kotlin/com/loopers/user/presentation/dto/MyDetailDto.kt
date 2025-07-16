package com.loopers.user.presentation.dto

import com.loopers.user.application.command.UserDetailResult
import com.loopers.user.domain.vo.GenderType

data class MyDetailResponse(
    val userId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    companion object {
        operator fun invoke(result: UserDetailResult): MyDetailResponse =
            MyDetailResponse(result.userId, result.email, result.birthDay, result.gender)
    }
}
