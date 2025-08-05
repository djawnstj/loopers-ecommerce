package com.loopers.infrastructure.point

import com.loopers.domain.point.UserPoint
import com.loopers.fixture.point.UserPointFixture
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

class UserPointRepositoryImplTest(
    private val cut: UserPointRepositoryImpl,
    private val jpaRepository: JpaUserPointRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class Save {
        @Test
        fun `포인트를 저장할 수 있다`() {
            // given
            val userPoint = UserPointFixture.기본.toEntity()

            // when
            cut.save(userPoint)

            // then
            val actual = jpaRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `포인트를 저장 후 해당 포인트를 조회하면 동일한 정보를 조회할 수 있다`() {
            // given
            val userPoint = UserPointFixture.기본.toEntity()

            // when
            cut.save(userPoint)

            // then
            val actual = jpaRepository.findByIdOrNull(userPoint.id)
            assertThat(actual).isNotNull
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal::class.java)
                .extracting("userId", "balance")
                .containsExactly(1L, BigDecimal("0.00"))
        }
    }

    @Nested
    inner class `회원 식별자로 포인트를 찾을 때` {
        @Test
        fun `해당하는 포인트가 찾지 못하면 null 을 반환 한다`() {
            // given
            val anyUserId = 1L

            // when
            val actual = cut.findByUserId(anyUserId)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `해당하는 포인트가 찾으면 UserPoint 객체에 담아 반환 한다`() {
            // given
            val anyUserId = 1L
            jpaRepository.saveAndFlush(UserPointFixture.기본.toEntity(userId = anyUserId))

            // when
            val actual = cut.findByUserId(anyUserId)

            // then
            assertThat(actual).isNotNull
                .extracting("userId", "balance")
                .containsExactly(1L, BigDecimal("0.00"))
        }

        @Test
        fun `삭제된 포인트는 조회되지 않는다`() {
            // given
            val userId = 1L
            val deletedUserPoint = UserPointFixture.기본.toEntity(userId = userId).apply(UserPoint::delete)
            jpaRepository.saveAndFlush(deletedUserPoint)

            // when
            val actual = cut.findByUserId(userId)

            // then
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class `비관적 락으로 회원 식별자로 포인트를 찾을 때` {
        @Test
        fun `해당하는 포인트가 없으면 null 을 반환한다`() {
            // given
            val anyUserId = 999L

            // when
            val actual = cut.findByUserIdWithPessimisticWrite(anyUserId)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `해당하는 포인트가 있으면 UserPoint 객체에 담아 반환한다`() {
            // given
            val anyUserId = 1L
            jpaRepository.saveAndFlush(UserPointFixture.기본.toEntity(userId = anyUserId))

            // when
            val actual = cut.findByUserIdWithPessimisticWrite(anyUserId)

            // then
            assertThat(actual).isNotNull
                .extracting("userId", "balance")
                .containsExactly(1L, BigDecimal("0.00"))
        }

        @Test
        fun `삭제된 포인트는 조회되지 않는다`() {
            // given
            val userId = 1L
            val deletedUserPoint = UserPointFixture.기본.toEntity(userId = userId).apply(UserPoint::delete)
            jpaRepository.saveAndFlush(deletedUserPoint)

            // when
            val actual = cut.findByUserIdWithPessimisticWrite(userId)

            // then
            assertThat(actual).isNull()
        }
    }
}
