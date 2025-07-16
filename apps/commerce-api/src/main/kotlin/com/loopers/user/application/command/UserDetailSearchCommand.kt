package com.loopers.user.application.command

import com.loopers.user.domain.User
import com.loopers.user.domain.vo.GenderType

data class UserDetailResult(
    val userId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    companion object {
        operator fun invoke(user: User): UserDetailResult =
            UserDetailResult(
                user.userId.value,
                user.email.value,
                user.birthDay.value,
                user.gender
            )
    }
}
