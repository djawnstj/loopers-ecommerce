package com.loopers.domain.user

import com.loopers.fixture.user.UserFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.domain.user.vo.BirthDay
import com.loopers.domain.user.vo.Email
import com.loopers.domain.user.vo.GenderType
import com.loopers.domain.user.vo.LoginId
import com.loopers.infrastructure.user.fake.TestUserRepository
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
            val user = UserFixture.기본.toEntity()
            userRepository.save(user)

            // when then
            assertThatThrownBy {
                cut.signUp(user)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.EXISTS_USER_LOGIN_ID, "이미 가입 된 ID 입니다.")
        }

        @Test
        fun `회원 가입 후 생성된 유저 정보를 반환 한다`() {
            // given
            val userRepository = TestUserRepository()
            val cut = UserServiceImpl(userRepository)
            val user = UserFixture.기본.toEntity()

            // when
            val actual = cut.signUp(user)

            // then
            assertThat(actual)
                .extracting("loginId", "email", "birthDay")
                .containsExactly("loginId", "email@domain.com", BirthDay("2025-01-01"))
        }

        @Test
        fun `회원 가입 시 회원 정보를 저장 한다`() {
            // given
            val userRepository = spyk(TestUserRepository())
            val cut = UserServiceImpl(userRepository)
            val user = UserFixture.기본.toEntity()

            // when
            cut.signUp(user)

            // then
            verify(exactly = 1) {
                userRepository.save(
                    match {
                        it.loginId == LoginId("loginId") &&
                                it.email == Email("email@domain.com") &&
                                it.birthDay == BirthDay("2025-01-01")
                    },
                )
            }
        }
    }

    @Nested
    inner class `로그인 ID 로 회원 정보를 조회할 때` {
        @Test
        fun `유저 저장소에서 ID 에 해당하는 회원을 찾는다`() {
            // given
            val userRepository = spyk(TestUserRepository())
            val cut = UserServiceImpl(userRepository)

            val user = userRepository.save(UserFixture.기본.toEntity())

            val loginId = user.loginId.value

            // when
            cut.getUserProfile(loginId)

            // then
            verify(exactly = 1) { userRepository.findByLoginId(LoginId("loginId")) }
        }

        @Test
        fun `해당 ID 의 회원이 없다면 CoreException UserNotFound 예외를 던진다`() {
            // given
            val userRepository = TestUserRepository()
            val cut = UserServiceImpl(userRepository)

            val userId = UserFixture.기본.loginId

            // when then
            assertThatThrownBy {
                cut.getUserProfile(userId)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.USER_NOT_FOUND, "로그인 ID가 loginId 에 해당하는 유저 정보를 찾지 못했습니다.")
        }

        @Test
        fun `해당 ID 의 회원이 존재할 경우 회원 정보가 반환 된다`() {
            // given
            val userRepository = TestUserRepository()
            val cut = UserServiceImpl(userRepository)

            val user = userRepository.save(UserFixture.기본.toEntity())

            val userId = user.loginId.value

            // when
            val actual = cut.getUserProfile(userId)

            // then
            assertThat(actual).extracting("loginId", "email", "birthDay", "gender")
                .containsExactly("loginId", "email@domain.com", BirthDay("2025-01-01"), GenderType.MEN)
        }
    }
}
