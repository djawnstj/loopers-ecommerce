package com.loopers.user.infrastructure

import com.loopers.fixture.user.UserFixture
import com.loopers.user.domain.vo.UserId
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserRepositoryImplTest(
    private val cut: UserRepositoryImpl,
    private val jpaRepository: JpaUserRepository,
) : IntegrationTestSupport() {
    @Nested
    inner class `동일한 UserId 가 존재하는지 조회할 때` {
        @Test
        fun `이미 동일한 UserId 가 있다면 true 를 반환 한다`() {
            // given
            jpaRepository.saveAndFlush(UserFixture.기본.toEntity())

            // when
            val actual = cut.existsByUserId(UserId("userId"))

            // then
            assertThat(actual).isTrue
        }

        @Test
        fun `이미 동일한 UserId 가 없다면 false 를 반환 한다`() {
            // given
            jpaRepository.saveAndFlush(UserFixture.기본.toEntity())

            // when
            val actual = cut.existsByUserId(UserId("userId1"))

            // then
            assertThat(actual).isFalse
        }
    }
}
