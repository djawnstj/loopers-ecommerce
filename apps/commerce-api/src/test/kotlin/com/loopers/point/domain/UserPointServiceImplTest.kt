package com.loopers.point.domain

import com.loopers.fixture.point.UserPointFixture
import com.loopers.point.domain.vo.Point
import com.loopers.point.fake.TestUserPointRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class UserPointServiceImplTest {

    @Nested
    inner class `회원의 초기 상태의 포인트를 생성할 때` {
        @Test
        fun `포인트를 0 포인트로 저장 한다`() {
            // given
            val userPointRepository = spyk(TestUserPointRepository())
            val cut = UserPointServiceImpl(userPointRepository)
            val anyUserId = 1L

            // when
            cut.createInitialPoint(anyUserId)

            // then
            verify {
                userPointRepository.save(
                    match {
                        it.userId == 1L &&
                                it.balance == Point(BigDecimal.ZERO)
                    },
                )
            }
        }

        @Test
        fun `저장한 포인트를 반환 한다`() {
            // given
            val userPointRepository = TestUserPointRepository()
            val cut = UserPointServiceImpl(userPointRepository)
            val anyUserId = 1L

            // when
            val actual = cut.createInitialPoint(anyUserId)

            // then
            assertThat(actual)
                .extracting("userId", "balance")
                .contains(1L, BigDecimal.ZERO)
        }
    }

    @Nested
    inner class `회원 식별자로 포인트를 충전할 때` {
        @Test
        fun `회원 식별자에 해당하는 포인트가 없으면 CoreException USER_POINT_NOT_FOUND 예외를 던진다`() {
            // given
            val userPointRepository = TestUserPointRepository()
            val cut = UserPointServiceImpl(userPointRepository)

            val anyUserId = 1L
            val amount = UserPointFixture.`양수 포인트`.balance

            // when then
            assertThatThrownBy {
                cut.chargePoint(anyUserId, amount)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.USER_POINT_NOT_FOUND, "회원 식별자 1 에 해당하는 포인트를 찾지 못했습니다.")
        }

        @Test
        fun `회원 식별자에 해당하는 포인트가 있다면 해당 포인트에 충전 한다`() {
            // given
            val userPointRepository = TestUserPointRepository()
            val cut = UserPointServiceImpl(userPointRepository)

            val userId = 1L
            val amount = UserPointFixture.`1000 포인트`.balance
            val userPoint = spyk(UserPointFixture.기본.toEntity(userId = userId))

            userPointRepository.save(userPoint)

            // when
            cut.chargePoint(userId, amount)

            // then
            verify(exactly = 1) { userPoint.charge(Point(BigDecimal(1000))) }
        }

        @Test
        fun `충전 후 충전 된 보유 총량을 반환 한다`() {
            // given
            val userPointRepository = TestUserPointRepository()
            val cut = UserPointServiceImpl(userPointRepository)

            val userId = 1L
            val amount = UserPointFixture.`1000 포인트`.balance
            val userPoint = UserPointFixture.`0 포인트`.toEntity(userId = userId)

            userPointRepository.save(userPoint)

            // when
            val actual = cut.chargePoint(userId, amount)

            // then
            assertThat(actual).isEqualTo(BigDecimal(1000))
        }
    }

    @Nested
    inner class `회원 식별자로 포인트를 조회할 때` {
        @Test
        fun `해당하는 포인트를 찾지 못하면 CoreException USER_POINT_NOT_FOUND 예외를 던진다`() {
            // given
            val userPointRepository = TestUserPointRepository()
            val cut = UserPointServiceImpl(userPointRepository)

            val anyUserId = 1L

            // when then
            assertThatThrownBy {
                cut.getUserPoint(anyUserId)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.USER_POINT_NOT_FOUND, "회원 식별자 1 에 해당하는 포인트를 찾지 못했습니다.")
        }

        @Test
        fun `해당하는 포인트를 찾으면 반환 한다`() {
            // given
            val userPointRepository = TestUserPointRepository()
            val cut = UserPointServiceImpl(userPointRepository)

            val userId = 1L
            val userPoint = UserPointFixture.`0 포인트`.toEntity(userId = userId)
            userPointRepository.save(userPoint)

            // when
            val actual = cut.getUserPoint(userId)

            // then
            assertThat(actual)
                .extracting("userId", "balance")
                .containsExactly(1L, BigDecimal.ZERO)
        }
    }
}
