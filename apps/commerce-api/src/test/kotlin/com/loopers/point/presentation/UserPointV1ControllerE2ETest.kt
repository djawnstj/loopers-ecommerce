package com.loopers.point.presentation

import com.loopers.fixture.point.UserPointFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.point.presentation.dto.ChargePointRequest
import com.loopers.point.presentation.dto.ChargePointResponse
import com.loopers.support.E2ETestSupport
import com.loopers.support.presentation.ApiResponse
import com.loopers.user.application.UserFacade
import com.loopers.user.application.command.UserCreateCommand
import com.loopers.user.presentation.dto.MyDetailResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import java.math.BigDecimal

class UserPointV1ControllerE2ETest(
    private val userFacade: UserFacade,
) : E2ETestSupport() {

    @Nested
    inner class `포인트 충전 요청을 받았을 때` {
        private val CHARGE_POINT_URL = "/api/v1/points/charge"

        @Test
        fun `존재하는 유저가 요청한 경우 충전 후 보유한 포인트 총량을 응답으로 반환 한다`() {
            // given
            val userFixture = UserFixture.기본
            val user = userFacade.createUser(UserCreateCommand(userFixture.userId, userFixture.email, userFixture.birthDay, userFixture.gender))

            val request = ChargePointRequest(UserPointFixture.`1000 포인트`.balance)
            val headers = HttpHeaders()
            headers["X-USER-ID"] = user.userId
            val httpRequest = HttpEntity(request, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<ChargePointResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(CHARGE_POINT_URL, HttpMethod.POST, httpRequest, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(200)) },
                { assertThat(actual.body?.data?.balance).isEqualTo(BigDecimal("1000.00")) },
            )
        }

        @Test
        fun `요청 바디에 userId 가 없다면 400 응답을 반환 한다`() {
            // given
            val userFixture = UserFixture.기본
            val user = userFacade.createUser(UserCreateCommand(userFixture.userId, userFixture.email, userFixture.birthDay, userFixture.gender))

            val request = "{}"
            val headers = HttpHeaders()
            headers["X-USER-ID"] = user.userId
            headers.contentType = MediaType.APPLICATION_JSON
            val httpRequest = HttpEntity(request, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<ChargePointResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(CHARGE_POINT_URL, HttpMethod.POST, httpRequest, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).isEqualTo("필수 필드 'amount'이(가) 누락되었습니다.") },
            )
        }

        @Test
        fun `요청 헤더에 X-USER-ID 헤더가 없는 경우 400 응답을 반환 한다`() {
            // given
            val responseType = object : ParameterizedTypeReference<ApiResponse<MyDetailResponse>>() {}

            // when
            val actual =
                testRestTemplate.exchange(CHARGE_POINT_URL, HttpMethod.POST, HttpEntity<Any>(HttpHeaders()), responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).isEqualTo("userId 가 누락되었습니다.") },
            )
        }
    }
}
