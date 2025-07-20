package com.loopers.user.domain.vo

import com.loopers.fixture.user.UserFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class EmailTest {
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class `이메일 값객체를 생성할 때` {

        @ParameterizedTest
        @MethodSource("provideInvalidEmails")
        fun `이메일 형식이 올바르지 않다면 예외를 던진다`(email: String) {
            // when then
            assertThatThrownBy { Email(email) }
                .isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.INVALID_USER_EMAIL_FORMAT, "이메일 형식이 올바르지 않습니다.")
        }

        @Test
        fun `올바른 이메일 형식이라면 이메일 객체를 생성할 수 있다`() {
            // given
            val email = UserFixture.`올바른 이메일`.email

            // when
            val actual = Email(email)

            // then
            assertThat(actual.value).isEqualTo("email@domain.com")
        }

        private fun provideInvalidEmails(): List<String> = listOf(
            UserFixture.`ID 만 존재하는 이메일`.email,
            UserFixture.`도메인, 도메인 확장자가 없는 이메일`.email,
            UserFixture.`도메인 확장자가 없는 이메일`.email,
            UserFixture.`도메인 라벨이 없는 이메일`.email,
            UserFixture.`도메인, 도메인 라벨이 없는 이메일`.email,
            UserFixture.`도메인이 없는 이메일`.email,
            UserFixture.`ID, @ 가 없는 이메일`.email,
            UserFixture.`ID 가 없는 이메일`.email,
        )
    }
}
