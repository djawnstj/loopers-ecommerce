package com.loopers.fixture.user

import com.loopers.user.domain.User
import com.loopers.user.domain.vo.GenderType

sealed class UserFixture(
    val userId: String = "userId",
    val email: String = "email@domain.com",
    val birthDay: String = "2025-01-01",
    val gender: GenderType = GenderType.MEN,
) {
    data object 기본 : UserFixture()

    data object `잘못된 형식의 생년월일` : UserFixture(birthDay = "20241231")

    fun toEntity(): User = User(userId, email, birthDay, gender)
}
