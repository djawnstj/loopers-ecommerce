package com.loopers.user.infrastructure

import com.loopers.fixture.user.UserFixture
import com.loopers.support.IntegrationTestSupport
import com.loopers.user.domain.vo.BirthDay
import com.loopers.user.domain.vo.GenderType
import com.loopers.user.domain.vo.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

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

    @Nested
    inner class Save {
        @Test
        fun `회원 정보를 저장할 수 있다`() {
            // given
            val user = UserFixture.기본.toEntity()

            // when
            cut.save(user)

            // then
            val actual = jpaRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `회원 정보를 저장 후 해당 회원을 조회하면 동일한 정보를 조회할 수 있다`() {
            // given
            val user = UserFixture.기본.toEntity()

            // when
            cut.save(user)

            // then
            val actual = jpaRepository.findByIdOrNull(user.id)
            assertThat(actual).isNotNull
                .extracting("userId", "email", "birthDay", "gender")
                .containsExactly("userId", "email@domain.com", BirthDay("2025-01-01"), GenderType.MEN)
        }
    }

    @Nested
    inner class `회원 ID 로 회원을 조회할 때` {
        @Test
        fun `해당하는 회원을 찾지 못하면 null 을 반환 한다`() {
            // given
            val userId = UserId(UserFixture.기본.userId)

            // when
            val actual = cut.findByUserId(userId)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `해당하는 회원을 찾으면 User 객체에 담아 반환 한다`() {
            // given
            val userId = jpaRepository.saveAndFlush(UserFixture.기본.toEntity()).userId

            // when
            val actual = cut.findByUserId(userId)

            // then
            assertThat(actual).isNotNull
                .extracting("userId", "email", "birthDay", "gender")
                .containsExactly("userId", "email@domain.com", BirthDay("2025-01-01"), GenderType.MEN)
        }
    }
}
