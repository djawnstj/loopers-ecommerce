package com.loopers.application.user.command

import com.loopers.domain.user.User
import com.loopers.domain.user.vo.GenderType

data class UserDetailResult(
    val loginId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    companion object {
        operator fun invoke(user: User): UserDetailResult =
            UserDetailResult(
                user.loginId.value,
                user.email.value,
                user.birthDay.value,
                user.gender
            )
    }
}
