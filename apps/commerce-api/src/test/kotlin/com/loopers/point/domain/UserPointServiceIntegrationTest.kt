package com.loopers.point.domain

import com.loopers.fixture.point.UserPointFixture
import com.loopers.point.infrastructure.JpaUserPointRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class UserPointServiceIntegrationTest(
    private val cut: UserPointService,
    private val userPointRepository: JpaUserPointRepository,
) : IntegrationTestSupport() {
    @Nested
    inner class `회원의 초기 상태의 포인트를 생성할 때` {
        @Test
        fun `0 포인트를 저장 한다`() {
            // given
            val anyUserId = 1L

            // when
            cut.createInitialPoint(anyUserId)

            // then
            val actual = userPointRepository.findAll()
            assertThat(actual).hasSize(1)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal::class.java)
                .extracting("userId", "balance")
                .containsExactly(Tuple.tuple(1L, BigDecimal("0.00")))
        }
    }

    @Nested
    inner class `회원 식별자로 포인트를 충전할 때` {
        @Test
        fun `해당하는 포인트를 찾아 충전 한다`() {
            // given
            val userId = 1L
            userPointRepository.saveAndFlush(UserPointFixture.`0 포인트`.toEntity(userId))

            // when
            cut.chargePoint(userId, UserPointFixture.`1000 포인트`.balance)

            // then
            val actual = userPointRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("userId", "balance")
                .containsExactly(Tuple.tuple(1L, BigDecimal("1000.00")))
        }
    }

    @Nested
    inner class `회원 식별자로 포인트를 찾을 때` {
        @Test
        fun `해당하는 포인트를 찾아 반환 한다`() {
            // given
            val userId = 1L
            userPointRepository.saveAndFlush(UserPointFixture.`0 포인트`.toEntity(userId))

            // when
            val actual = cut.getUserPoint(userId)

            // then
            assertThat(actual)
                .extracting("userId", "balance")
                .containsExactly(1L, BigDecimal("0.00"))
        }
    }
}
