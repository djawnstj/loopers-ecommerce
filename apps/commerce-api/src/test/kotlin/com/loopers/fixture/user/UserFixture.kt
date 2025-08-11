package com.loopers.fixture.user

import com.loopers.domain.user.User
import com.loopers.domain.user.vo.GenderType

sealed class UserFixture(
    val loginId: String = "loginId",
    val email: String = "email@domain.com",
    val birthDay: String = "2025-01-01",
    val gender: GenderType = GenderType.MEN,
) {
    data object 기본 : UserFixture()
    data object `로그인 ID 1` : UserFixture(loginId = "loginId1")
    data object `로그인 ID 2` : UserFixture(loginId = "loginId2")
    data object `로그인 ID 3` : UserFixture(loginId = "loginId3")

    data object `잘못된 형식의 생년월일` : UserFixture(birthDay = "20241231")

    data object `ID 만 존재하는 이메일` : UserFixture(email = "email")
    data object `도메인, 도메인 확장자가 없는 이메일` : UserFixture(email = "email@")
    data object `도메인 확장자가 없는 이메일` : UserFixture(email = "email@domain")
    data object `도메인 라벨이 없는 이메일` : UserFixture(email = "email@domain.")
    data object `도메인, 도메인 라벨이 없는 이메일` : UserFixture(email = "email@.")
    data object `도메인이 없는 이메일` : UserFixture(email = "email@.com")
    data object `ID, @ 가 없는 이메일` : UserFixture(email = "domain.com")
    data object `ID 가 없는 이메일` : UserFixture(email = "@domain.com")
    data object `올바른 이메일` : UserFixture(email = "email@domain.com")

    data object `6자 미만 로그인 ID` : UserFixture(loginId = "abcde")
    data object `10자 초과 로그인 ID` : UserFixture(loginId = "abcdefghijk")
    data object `특수문자 포함 로그인 ID` : UserFixture(loginId = "abc-def")
    data object `공백 포함 로그인 ID` : UserFixture(loginId = "abc def")
    data object `한글 로그인 ID` : UserFixture(loginId = "한글아이디")
    data object `빈 로그인 ID` : UserFixture(loginId = "")
    data object `영어 소문자 6자 로그인 ID` : UserFixture(loginId = "abcdef")
    data object `영어 소문자 + 숫자 6자 로그인 ID` : UserFixture(loginId = "abcde1")
    data object `숫자 6자 로그인 ID` : UserFixture(loginId = "123456")
    data object `영어 대문자 6자 로그인 ID` : UserFixture(loginId = "ABCDEF")
    data object `영어 대문자 + 숫자 6자 로그인 ID` : UserFixture(loginId = "ABCDE1")
    data object `영어 대문자 + 소문자 6자 로그인 ID` : UserFixture(loginId = "ABCDEf")
    data object `영어 대문자 + 소문자 + 숫자 6자 로그인 ID` : UserFixture(loginId = "ABCDe1")
    data object `영어 소문자 10자 로그인 ID` : UserFixture(loginId = "abcdefghij")
    data object `영어 소문자 + 숫자 10자 로그인 ID` : UserFixture(loginId = "abcdefghi1")
    data object `영어 대문자 로그인 ID` : UserFixture(loginId = "ABCDEFGHIJ")
    data object `영어 대문자 + 소문자 10자 로그인 ID` : UserFixture(loginId = "ABCDEFGHIj")
    data object `영어 대문자 + 소문자 + 숫자 10자 로그인 ID` : UserFixture(loginId = "ABCDEFGHi1")

    fun toEntity(): User = User(loginId, email, birthDay, gender)

    companion object {
        fun create(loginId: String, email: String = "email@domain.com", birthDay: String = "2025-01-01", gender: GenderType = GenderType.MEN): User =
            User(loginId, email, birthDay, gender)
    }
}
