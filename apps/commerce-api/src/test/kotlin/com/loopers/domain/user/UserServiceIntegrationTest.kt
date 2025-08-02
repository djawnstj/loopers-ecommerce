package com.loopers.domain.user

import com.loopers.fixture.user.UserFixture
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.domain.user.vo.BirthDay
import com.loopers.domain.user.vo.GenderType
import com.loopers.infrastructure.user.JpaUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
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
            val user = UserFixture.기본.toEntity()
            userRepository.save(user)

            // when
            assertThatThrownBy {
                cut.signUp(user)
            }

            // then
            val actual = userRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `회원 가입 시 회원 정보를 저장 한다`() {
            // given
            val user = UserFixture.기본.toEntity()

            // when
            cut.signUp(user)

            // then
            val actual = userRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("loginId", "email", "birthDay", "gender")
                .containsExactly(Tuple.tuple("loginId", "email@domain.com", BirthDay("2025-01-01"), GenderType.MEN))
        }
    }

    @Nested
    inner class `로그인 ID 로 회원 정보를 조회할 때` {
        @Test
        fun `해당 ID 의 회원을 찾을 수 없다면 CoreException UserNotFound 예외를 던진다`() {
            // given
            val loginId = UserFixture.기본.loginId

            // when then
            assertThatThrownBy {
                cut.getUserProfile(loginId)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.USER_NOT_FOUND, "로그인 ID가 loginId 에 해당하는 유저 정보를 찾지 못했습니다.")
        }

        @Test
        fun `해당 ID 의 회원을 찾을 수 있다면 회원 정보를 반환 한다`() {
            // given
            val user = userRepository.save(UserFixture.기본.toEntity())

            val loginId = user.loginId.value

            // when
            val actual = cut.getUserProfile(loginId)

            // then
            assertThat(actual).extracting("loginId", "email", "birthDay", "gender")
                .containsExactly("loginId", "email@domain.com", BirthDay("2025-01-01"), GenderType.MEN)
        }
    }
}
