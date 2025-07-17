package com.loopers.point.presentation

import com.loopers.fixture.point.UserPointFixture
import com.loopers.point.presentation.dto.ChargePointRequest
import com.loopers.point.presentation.dto.ChargePointResponse
import com.loopers.support.E2ETestSupport
import com.loopers.support.presentation.ApiResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import java.math.BigDecimal

class UserPointV1ControllerE2ETest : E2ETestSupport() {

    @Nested
    inner class `포인트 충전 요청을 받았을 때` {
        @Test
        fun `존재하는 유저가 요청한 경우 충전 후 보유한 포인트 총량을 응답으로 반환 한다`() {
            // given
            val amount = UserPointFixture.`1000 포인트`.point
            val request = ChargePointRequest(amount)
            val httpRequest = HttpEntity(request)
            val responseType = object : ParameterizedTypeReference<ApiResponse<ChargePointResponse>>() {}

            // when
            val actual = testRestTemplate.exchange("/api/v1/points/charge", HttpMethod.POST, httpRequest, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(200)) },
                { assertThat(actual.body?.data?.balance).isEqualTo(BigDecimal.ZERO) },
            )
        }
    }
}
