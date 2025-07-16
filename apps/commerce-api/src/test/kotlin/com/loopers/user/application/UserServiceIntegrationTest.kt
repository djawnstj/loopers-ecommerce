package com.loopers.user.application

import com.loopers.fixture.user.UserFixture
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.user.application.command.UserSignUpCommand
import com.loopers.user.domain.vo.GenderType
import com.loopers.user.infrastructure.JpaUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserServiceIntegrationTest(
    private val cut: UserService,
    private val userRepository: JpaUserRepository,
) : IntegrationTestSupport() {
    @Nested
    inner class `회원 가입을 할 때` {
        @Test
        fun `이미 가입된 ID 로 회원 가입을 시도하면 실패한다`() {
            // given
            val userFixture = UserFixture.기본
            userRepository.save(userFixture.toEntity())

            val command = UserSignUpCommand(userFixture.userId, userFixture.email, userFixture.birthDay, userFixture.gender)

            // when
            assertThatThrownBy {
                cut.signUp(command)
            }

            // then
            val actual = userRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `회원 가입 시 회원 정보를 저장 한다`() {
            // given
            val userFixture = UserFixture.기본

            val command = UserSignUpCommand(userFixture.userId, userFixture.email, userFixture.birthDay, userFixture.gender)

            // when
            cut.signUp(command)

            // then
            val actual = userRepository.findAll()
            assertThat(actual).hasSize(1)
        }
    }

    @Nested
    inner class `회원 ID 로 회원 정보를 조회할 때` {
        @Test
        fun `해당 ID 의 회원을 찾을 수 없다면 CoreException UserNotFound 예외를 던진다`() {
            // given
            val userId = UserFixture.기본.userId

            // when then
            assertThatThrownBy {
                cut.searchDetailByUserId(userId)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.USER_NOT_FOUND, "회원 ID가 userId 에 해당하는 유저 정보를 찾지 못했습니다.")
        }

        @Test
        fun `해당 ID 의 회원을 찾을 수 있다면 회원 정보를 반환 한다`() {
            // given
            val user = userRepository.save(UserFixture.기본.toEntity())

            val userId = user.userId.value

            // when
            val actual = cut.searchDetailByUserId(userId)

            // then
            assertThat(actual).extracting("userId", "email", "birthDay", "gender")
                .containsExactly("userId", "email@domain.com", "2025-01-01", GenderType.MEN)
        }
    }
}
