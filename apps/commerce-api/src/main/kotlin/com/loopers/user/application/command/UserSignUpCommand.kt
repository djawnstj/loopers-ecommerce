package com.loopers.user.application.command

import com.loopers.user.domain.User
import com.loopers.user.domain.vo.GenderType

data class UserSignUpCommand(
    val userId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    fun toEntity(): User = User(userId, email, birthDay, gender)
}

data class UserSignUpResult(
    val userId: String,
    val email: String,
    val birthDay: String,
    val gender: GenderType,
) {
    companion object {
        fun fromEntity(entity: User): UserSignUpResult =
            UserSignUpResult(entity.userId.value, entity.email.value, entity.birthDay.value, entity.gender)
    }
}
