package com.loopers.user.domain.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UserIdTest {
    @Nested
    inner class `로그인 아이디 값객체를 생성할 때` {

        @ParameterizedTest
        @ValueSource(
            strings = [
                "abcde",
                "abcdefghijk",
                "abc-def",
                "abc def",
                "abc@def",
                "한글아이디",
                "",
                "abc!def",
            ],
        )
        fun `로그인 아이디 형식이 올바르지 않다면 예외를 던진다`(userId: String) {
            // when then
            assertThatThrownBy { UserId(userId) }
                .isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.INVALID_USER_ID_FORMAT, "회원 ID 는 6자 이상 10자 이내의 영문 및 숫자만 허용됩니다.")
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "abcdef",
                "abcde1",
                "a12345",
                "123456",
                "AbCDE1",
                "A12345",
                "abcdefghij",
                "abcdefghi1",
                "a123456789",
                "AbCDEFGHI1",
                "A123456789",
            ],
        )
        fun `올바른 로그인 아이디 형식이라면 로그인 아이디 객체를 생성할 수 있다`(userId: String) {
            // when
            val actual = UserId(userId)

            // then
            assertThat(actual.value).isEqualTo(userId)
        }
    }
}
