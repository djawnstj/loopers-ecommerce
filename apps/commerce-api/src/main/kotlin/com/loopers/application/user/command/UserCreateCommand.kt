package com.loopers.application.user.command

import com.loopers.domain.user.User
import com.loopers.domain.user.vo.GenderType

data class UserCreateCommand(
    val loginId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    fun toEntity(): User = User(loginId, email, birthDay, gender)
}

data class UserCreateResult(
    val loginId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    companion object {
        operator fun invoke(entity: User): UserCreateResult =
            UserCreateResult(entity.loginId.value, entity.email.value, entity.birthDay.value, entity.gender)
    }
}
