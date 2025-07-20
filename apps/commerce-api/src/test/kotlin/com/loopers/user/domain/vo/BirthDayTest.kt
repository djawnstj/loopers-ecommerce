package com.loopers.user.domain.vo

import com.loopers.fixture.user.UserFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class BirthDayTest {
    @Nested
    inner class `생년월일 값객체를 생성할 때` {

        @Test
        fun `생년월일 형식이 yyyy-MM-dd 형식이 아니라면 예외를 던진다`() {
            // given
            val invalidFormatBirthDay = UserFixture.`잘못된 형식의 생년월일`.birthDay

            // when then
            assertThatThrownBy { BirthDay(invalidFormatBirthDay) }
                .isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.INVALID_USER_BIRTH_DAY_FORMAT, "생년월일 형식은 'yyyy-MM-dd' 형식이어야 합니다.")
        }

        @Test
        fun `생년월일이 현재 날짜와 같다면 예외를 던진다`() {
            // given
            val fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T00:00:00Z"),
                ZoneId.systemDefault(),
            )
            val invalidBirthDay = "2025-01-01"

            // when then
            assertThatThrownBy { BirthDay(invalidBirthDay, fixedClock) }
                .isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.REQUIRED_USER_BIRTH_DAY_AFTER_THEN_NOW, "생년월일은 현재 날짜보다 이전이어야 합니다.")
        }

        @Test
        fun `생년월일이 현재 날짜보다 이후면 예외를 던진다`() {
            // given
            val fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T00:00:00Z"),
                ZoneId.systemDefault(),
            )
            val invalidBirthDay = "2025-01-02"

            // when then
            assertThatThrownBy { BirthDay(invalidBirthDay, fixedClock) }
                .isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.REQUIRED_USER_BIRTH_DAY_AFTER_THEN_NOW, "생년월일은 현재 날짜보다 이전이어야 합니다.")
        }

        @Test
        fun `생년월일 형식이 yyyy-MM-dd 면서 현재 날짜보다 이전이라면 생년월일 객체를 생성할 수 있다`() {
            // given
            val fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T00:00:00Z"),
                ZoneId.systemDefault(),
            )
            val birthDay = "2024-12-31"

            // when
            val actual = BirthDay(birthDay, fixedClock)

            // then
            assertThat(actual.value).isEqualTo("2024-12-31")
        }
    }
}
