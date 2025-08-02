package com.loopers.application.user

import com.loopers.fixture.user.UserFixture
import com.loopers.infrastructure.point.JpaUserPointRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.application.user.command.UserCreateCommand
import com.loopers.domain.user.vo.BirthDay
import com.loopers.domain.user.vo.GenderType
import com.loopers.infrastructure.user.JpaUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class UserFacadeIntegrationTest(
    private val cut: UserFacade,
    private val userRepository: JpaUserRepository,
    private val userPointRepository: JpaUserPointRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `새로운 회원을 생성할 때` {
        @Test
        fun `생성하고자 하는 회원 정보가 저장 된다`() {
            // given
            val fixture = UserFixture.기본
            val command = UserCreateCommand(fixture.loginId, fixture.email, fixture.birthDay, fixture.gender)

            // when
            cut.createUser(command)

            // then
            val actual = userRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("loginId", "email", "birthDay", "gender")
                .containsExactly(Tuple.tuple("loginId", "email@domain.com", BirthDay("2025-01-01"), GenderType.MEN))
        }

        @Test
        fun `초기 포인트 값이 저장 된다`() {
            // given
            val fixture = UserFixture.기본
            val command = UserCreateCommand(fixture.loginId, fixture.email, fixture.birthDay, fixture.gender)

            // when
            cut.createUser(command)

            // then
            val actual = userPointRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("userId", "balance")
                .containsExactly(Tuple.tuple(1L, BigDecimal("0.00")))
        }

        @Test
        fun `생성한 회원 정보를 반환 한다`() {
            // given
            val fixture = UserFixture.기본
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
