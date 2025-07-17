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

    data object `ID 만 존재하는 이메일` : UserFixture(email = "email")
    data object `도메인, 도메인 확장자가 없는 이메일` : UserFixture(email = "email@")
    data object `도메인 확장자가 없는 이메일` : UserFixture(email = "email@domain")
    data object `도메인 라벨이 없는 이메일` : UserFixture(email = "email@domain.")
    data object `도메인, 도메인 라벨이 없는 이메일` : UserFixture(email = "email@.")
    data object `도메인이 없는 이메일` : UserFixture(email = "email@.com")
    data object `ID, @ 가 없는 이메일` : UserFixture(email = "domain.com")
    data object `ID 가 없는 이메일` : UserFixture(email = "@domain.com")
    data object `올바른 이메일` : UserFixture(email = "@domain.com")

    data object `6자 미만 로그인 ID` : UserFixture(userId = "abcde")
    data object `10자 초과 로그인 ID` : UserFixture(userId = "abcdefghijk")
    data object `특수문자 포함 로그인 ID` : UserFixture(userId = "abc-def")
    data object `공백 포함 로그인 ID` : UserFixture(userId = "abc def")
    data object `한글 로그인 ID` : UserFixture(userId = "한글아이디")
    data object `빈 로그인 ID` : UserFixture(userId = "")
    data object `영어 소문자 6자 로그인 ID` : UserFixture()
    data object `영어 소문자 + 숫자 6자 로그인 ID` : UserFixture()
    data object `숫자 6자 로그인 ID` : UserFixture()
    data object `영어 대문자 6자 로그인 ID` : UserFixture()
    data object `영어 대문자 + 숫자 6자 로그인 ID` : UserFixture()
    data object `영어 대문자 + 소문자 6자 로그인 ID` : UserFixture()
    data object `영어 대문자 + 소문자 + 숫자 6자 로그인 ID` : UserFixture()
    data object `영어 소문자 10자 로그인 ID` : UserFixture()
    data object `영어 소문자 + 숫자 10자 로그인 ID` : UserFixture()
    data object `영어 대문자 로그인 ID` : UserFixture()
    data object `영어 대문자 + 소문자 10자 로그인 ID` : UserFixture()
    data object `영어 대문자 + 소문자 + 숫자 10자 로그인 ID` : UserFixture()


    fun toEntity(): User = User(userId, email, birthDay, gender)
}
