package com.loopers.point.application

import com.loopers.fixture.point.UserPointFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.point.infrastructure.JpaUserPointRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.user.infrastructure.JpaUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class UserPointFacadeIntegrationTest(
    private val cut: UserPointFacade,
    private val userPointRepository: JpaUserPointRepository,
    private val userRepository: JpaUserRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `로그인 ID 로 포인트를 증가시킬 때` {
        @Test
        fun `존재하지 않는 로그인 ID 로 충전을 시도한 경우 CoreException USER_NOT_FOUND 예외를 던진다`() {
            // given
            val notExistsUserId = "anyUserId"
            val amount = UserPointFixture.`1000 포인트`.balance

            // when then
            assertThatThrownBy {
                cut.increasePoint(notExistsUserId, amount)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.USER_NOT_FOUND, "로그인 ID가 anyUserId 에 해당하는 유저 정보를 찾지 못했습니다.")
        }

        @Test
        fun `해당하는 회원의 포인트를 증가시킨다`() {
            // given
            val user = userRepository.saveAndFlush(UserFixture.기본.toEntity())
            userPointRepository.saveAndFlush(UserPointFixture.`0 포인트`.toEntity(userId = user.id))

            val loginId = user.loginId.value
            val amount = UserPointFixture.`1000 포인트`.balance

            // when
            cut.increasePoint(loginId, amount)

            // then
            val actual = userPointRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("userId", "balance")
                .containsExactly(Tuple.tuple(user.id, BigDecimal("1000.00")))
        }

        @Test
        fun `증가 된 포인트를 반환 한다`() {
            // given
            val user = userRepository.saveAndFlush(UserFixture.기본.toEntity())
            userPointRepository.saveAndFlush(UserPointFixture.`0 포인트`.toEntity(userId = user.id))

            val loginId = user.loginId.value
            val amount = UserPointFixture.`1000 포인트`.balance

            // when
            val actual = cut.increasePoint(loginId, amount)

            // then
            assertThat(actual).extracting("balance").isEqualTo(BigDecimal("1000.00"))
        }
    }

    @Nested
    inner class `로그인 ID 로 포인트 잔액을 가져올 때` {
        @Test
        fun `해당 ID 회원이 존재할 경우 보유 포인트가 반환된다`() {
            // given
            val user = userRepository.saveAndFlush(UserFixture.기본.toEntity())
            userPointRepository.saveAndFlush(UserPointFixture.`0 포인트`.toEntity(userId = user.id))

            val loginId = user.loginId.value

            // when
            val actual = cut.getPointBalance(loginId)

            // then
            assertThat(actual)
                .extracting("balance")
                .isEqualTo(BigDecimal("0.00"))
        }
    }
}
