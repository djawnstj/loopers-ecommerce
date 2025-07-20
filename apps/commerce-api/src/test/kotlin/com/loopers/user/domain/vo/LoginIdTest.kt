package com.loopers.user.domain.vo

import com.loopers.fixture.user.UserFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class LoginIdTest {
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `로그인 아이디 값객체를 생성할 때` {

        @ParameterizedTest
        @MethodSource("provideInvalidLoginId")
        fun `로그인 아이디 형식이 올바르지 않다면 예외를 던진다`(loginId: String) {
            // when then
            assertThatThrownBy { LoginId(loginId) }
                .isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.INVALID_USER_ID_FORMAT, "로그인 ID 는 6자 이상 10자 이내의 영문 및 숫자만 허용됩니다.")
        }

        @ParameterizedTest
        @MethodSource("provideValidLoginId")
        fun `올바른 로그인 아이디 형식이라면 로그인 아이디 객체를 생성할 수 있다`(loginId: String) {
            // when
            val actual = LoginId(loginId)

            // then
            assertThat(actual.value).isEqualTo(loginId)
        }

        private fun provideInvalidLoginId(): List<String> =
            listOf(
                UserFixture.`6자 미만 로그인 ID`.loginId,
                UserFixture.`10자 초과 로그인 ID`.loginId,
                UserFixture.`특수문자 포함 로그인 ID`.loginId,
                UserFixture.`공백 포함 로그인 ID`.loginId,
                UserFixture.`한글 로그인 ID`.loginId,
                UserFixture.`빈 로그인 ID`.loginId,
            )

        private fun provideValidLoginId(): List<String> =
            listOf(
                UserFixture.`영어 소문자 6자 로그인 ID`.loginId,
                UserFixture.`영어 소문자 + 숫자 6자 로그인 ID`.loginId,
                UserFixture.`숫자 6자 로그인 ID`.loginId,
                UserFixture.`영어 대문자 6자 로그인 ID`.loginId,
                UserFixture.`영어 대문자 + 숫자 6자 로그인 ID`.loginId,
                UserFixture.`영어 대문자 + 소문자 6자 로그인 ID`.loginId,
                UserFixture.`영어 대문자 + 소문자 + 숫자 6자 로그인 ID`.loginId,
                UserFixture.`영어 소문자 10자 로그인 ID`.loginId,
                UserFixture.`영어 소문자 + 숫자 10자 로그인 ID`.loginId,
                UserFixture.`영어 대문자 로그인 ID`.loginId,
                UserFixture.`영어 대문자 + 소문자 10자 로그인 ID`.loginId,
                UserFixture.`영어 대문자 + 소문자 + 숫자 10자 로그인 ID`.loginId,
            )
    }
}
