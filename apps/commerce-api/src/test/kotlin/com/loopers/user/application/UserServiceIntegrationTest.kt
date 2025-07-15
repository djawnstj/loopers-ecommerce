package com.loopers.user.application

import com.loopers.fixture.user.UserFixture
import com.loopers.support.IntegrationTestSupport
import com.loopers.user.application.command.UserSignUpCommand
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
}
