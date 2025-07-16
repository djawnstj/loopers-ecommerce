package com.loopers.user.domain

import com.loopers.user.domain.vo.GenderType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `회원을 생성할 수 있다`() {
        // given
        val userId = "test123"
        val email = "test@example.com"
        val birthDay = "1990-01-01"
        val gender = GenderType.MEN

        // when
        val user = User(userId, email, birthDay, gender)

        // then
        assertThat(user.userId.value).isEqualTo("test123")
        assertThat(user.email.value).isEqualTo("test@example.com")
        assertThat(user.birthDay.value).isEqualTo("1990-01-01")
        assertThat(user.gender).isEqualTo(GenderType.MEN)
    }
}
