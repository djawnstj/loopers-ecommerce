package com.loopers.user.application

import com.loopers.fixture.user.UserFixture
import com.loopers.point.domain.UserPointService
import com.loopers.user.application.command.UserCreateCommand
import com.loopers.user.domain.User
import com.loopers.user.domain.UserService
import com.loopers.user.domain.vo.BirthDay
import com.loopers.user.domain.vo.Email
import com.loopers.user.domain.vo.GenderType
import com.loopers.user.domain.vo.LoginId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserFacadeTest {
    @Nested
    inner class `새로운 회원을 생성할 때` {
        @Test
        fun `회원 가입을 한다`() {
            // given
            val userService: UserService = mockk()
            val cut = UserFacade(userService, mockk(relaxed = true))
            val fixture = UserFixture.기본

            every {
                userService.signUp(
                    match {
                        it.loginId == LoginId("loginId") &&
                                it.email == Email("email@domain.com") &&
                                it.birthDay == BirthDay("2025-01-01") &&
                                it.gender == GenderType.MEN
                    },
                )
            } returnsArgument 0

            val command = UserCreateCommand(fixture.loginId, fixture.email, fixture.birthDay, fixture.gender)

            // when
            cut.createUser(command)

            // then
            verify(exactly = 1) {
                userService.signUp(
                    match {
                        it.loginId == LoginId("loginId") &&
                                it.email == Email("email@domain.com") &&
                                it.birthDay == BirthDay("2025-01-01") &&
                                it.gender == GenderType.MEN
                    },
                )
            }
        }

        @Test
        fun `생성된 회원의 id 로 초기 포인트를 생성 한다`() {
            // given
            val userService: UserService = mockk()
            val userPointService: UserPointService = mockk()
            val cut = UserFacade(userService, userPointService)
            val fixture = UserFixture.기본
            val user: User = mockk(relaxed = true)
            val userId = 1L

            every { userService.signUp(any()) } returns user
            every { user.id } returns userId
            every { userPointService.createInitialPoint(1L) } returns mockk()

            val command = UserCreateCommand(fixture.loginId, fixture.email, fixture.birthDay, fixture.gender)

            // when
            cut.createUser(command)

            // then
            verify(exactly = 1) { userPointService.createInitialPoint(1L) }
        }

        @Test
        fun `생성된 회원 정보를 반환 한다`() {
            // given
            val userService: UserService = mockk()
            val cut = UserFacade(userService, mockk(relaxed = true))
            val fixture = UserFixture.기본

            every {
                userService.signUp(
                    match {
                        it.loginId == LoginId("loginId") &&
                                it.email == Email("email@domain.com") &&
                                it.birthDay == BirthDay("2025-01-01") &&
                                it.gender == GenderType.MEN
                    },
                )
            } returnsArgument 0

            val command = UserCreateCommand(fixture.loginId, fixture.email, fixture.birthDay, fixture.gender)

            // when
            val actual = cut.createUser(command)

            // then
            assertThat(actual)
                .extracting("loginId", "email", "birthDay", "gender")
                .containsExactly("loginId", "email@domain.com", "2025-01-01", GenderType.MEN)
        }
    }
}
