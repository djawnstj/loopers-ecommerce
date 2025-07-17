package com.loopers.point.infrastructure

import com.loopers.fixture.point.UserPointFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.point.domain.vo.Point
import com.loopers.support.IntegrationTestSupport
import com.loopers.user.domain.vo.BirthDay
import com.loopers.user.domain.vo.GenderType
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
    }
}
