package com.loopers.point.application

import com.loopers.fixture.point.UserPointFixture
import com.loopers.point.domain.UserPointService
import com.loopers.user.domain.User
import com.loopers.user.domain.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class UserPointFacadeTest {

    @Nested
    inner class `회원 ID 로 포인트를 증가시킬 때` {
        @Test
        fun `회원 정보를 가져온다`() {
            // given
            val userPointService: UserPointService = mockk(relaxed = true)
            val userService: UserService = mockk(relaxed = true)

            val cut = UserPointFacade(userPointService, userService)

            val anyUserId = "userId"
            val amount = UserPointFixture.`1000 포인트`.balance

            // when
            cut.increasePoint(anyUserId, amount)

            // then
            verify(exactly = 1) { userService.getUserProfile("userId") }
        }

        @Test
        fun `해당 회원의 포인트를 충전한다`() {
            // given
            val userPointService: UserPointService = mockk(relaxed = true)
            val userService: UserService = mockk()

            val cut = UserPointFacade(userPointService, userService)

            val anyUserId = "userId"
            val amount = UserPointFixture.`1000 포인트`.balance
            val user: User = mockk()
            val userId = 1L

            every { userService.getUserProfile(anyUserId) } returns user
            every { user.id } returns userId

            // when
            cut.increasePoint(anyUserId, amount)

            // then
            verify(exactly = 1) { userPointService.chargePoint(1, BigDecimal(1000)) }
        }

        @Test
        fun `충전 된 포인트를 반환 한다`() {
            // given
            val userPointService: UserPointService = mockk(relaxed = true)
            val userService: UserService = mockk()

            val cut = UserPointFacade(userPointService, userService)

            val anyUserId = "userId"
            val amount = UserPointFixture.`1000 포인트`.balance
            val user: User = mockk()
            val userId = 1L

            every { userService.getUserProfile(anyUserId) } returns user
            every { user.id } returns userId
            every { userPointService.chargePoint(1, BigDecimal(1000)) } returns BigDecimal(1000)

            // when
            val actual = cut.increasePoint(anyUserId, amount)

            // then
            assertThat(actual)
                .extracting("balance")
                .isEqualTo(BigDecimal(1000))
        }
    }

    @Nested
    inner class `회원 ID 로 포인트 잔액을 가져올 때` {
        @Test
        fun `회원 정보를 가져온다`() {
            // given
            val userPointService: UserPointService = mockk(relaxed = true)
            val userService: UserService = mockk(relaxed = true)

            val cut = UserPointFacade(userPointService, userService)

            val anyUserId = "userId"

            // when
            cut.getPointBalance(anyUserId)

            // then
            verify(exactly = 1) { userService.getUserProfile("userId") }
        }

        @Test
        fun `해당 회원의 포인트를 가져온다`() {
            // given
            val userPointService: UserPointService = mockk()
            val userService: UserService = mockk()

            val cut = UserPointFacade(userPointService, userService)

            val anyUserId = "userId"
            val user: User = mockk()
            val userId = 1L
            val userPoint = UserPointFixture.`1000 포인트`.toEntity()

            every { userService.getUserProfile(anyUserId) } returns user
            every { user.id } returns userId
            every { userPointService.getUserPoint(userId) } returns userPoint

            // when
            cut.getPointBalance(anyUserId)

            // then
            verify(exactly = 1) { userPointService.getUserPoint(1) }
        }

        @Test
        fun `포인트의 잔액을 반환 한다`() {
            // given
            val userPointService: UserPointService = mockk()
            val userService: UserService = mockk()

            val cut = UserPointFacade(userPointService, userService)

            val anyUserId = "userId"
            val user: User = mockk()
            val userId = 1L
            val userPoint = UserPointFixture.`1000 포인트`.toEntity()

            every { userService.getUserProfile(anyUserId) } returns user
            every { user.id } returns userId
            every { userPointService.getUserPoint(userId) } returns userPoint

            // when
            val actual = cut.getPointBalance(anyUserId)

            // then
            assertThat(actual)
                .extracting("balance")
                .isEqualTo(BigDecimal(1000))
        }
    }
}
