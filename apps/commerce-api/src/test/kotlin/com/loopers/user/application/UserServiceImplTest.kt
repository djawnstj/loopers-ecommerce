package com.loopers.user.application

import com.loopers.fixture.user.UserFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.user.application.command.UserSignUpCommand
import com.loopers.user.domain.vo.BirthDay
import com.loopers.user.domain.vo.Email
import com.loopers.user.domain.vo.UserId
import com.loopers.user.fake.TestUserRepository
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserServiceImplTest {

    @Nested
    inner class `회원 가입을 할 때` {
        @Test
        fun `이미 가입된 ID 로 회원 가입을 시도하면 예외를 던진다`() {
            // given
            val userRepository = TestUserRepository()
            val cut = UserServiceImpl(userRepository)
            val userFixture = UserFixture.기본
            userRepository.save(userFixture.toEntity())

            val command = UserSignUpCommand(userFixture.userId, userFixture.email, userFixture.birthDay, userFixture.gender)

            // when then
            assertThatThrownBy {
                cut.signUp(command)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.EXISTS_USER_LOGIN_ID, "이미 가입 된 ID 입니다.")
        }

        @Test
        fun `회원 가입 후 생성된 유저 정보를 반환 한다`() {
            // given
            val userRepository = TestUserRepository()
            val cut = UserServiceImpl(userRepository)
            val userFixture = UserFixture.기본
            val command = UserSignUpCommand(userFixture.userId, userFixture.email, userFixture.birthDay, userFixture.gender)

            // when
            val actual = cut.signUp(command)

            // then
            assertThat(actual)
                .extracting("userId", "email", "birthDay")
                .containsExactly("userId", "email@domain.com", "2025-01-01")
        }

        @Test
        fun `회원 가입 시 회원 정보를 저장 한다`() {
            // given
            val userRepository = spyk(TestUserRepository())
            val cut = UserServiceImpl(userRepository)
            val userFixture = UserFixture.기본
            val command = UserSignUpCommand(userFixture.userId, userFixture.email, userFixture.birthDay, userFixture.gender)

            // when
            cut.signUp(command)

            // then
            verify(exactly = 1) {
                userRepository.save(
                    match {
                        it.userId == UserId("userId") &&
                                it.email == Email("email@domain.com") &&
                                it.birthDay == BirthDay("2025-01-01")
                    },
                )
            }
        }
    }
}
