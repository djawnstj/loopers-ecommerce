package com.loopers.user.presentation

import com.loopers.fixture.user.UserFixture
import com.loopers.support.E2ETestSupport
import com.loopers.support.presentation.ApiResponse
import com.loopers.user.application.UserFacade
import com.loopers.user.application.command.UserCreateCommand
import com.loopers.user.domain.vo.GenderType
import com.loopers.user.presentation.dto.MyDetailResponse
import com.loopers.user.presentation.dto.SignUpRequest
import com.loopers.user.presentation.dto.SignUpResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType

class UserV1ControllerE2ETest(
    private val userFacade: UserFacade,
) : E2ETestSupport() {
    @Nested
    inner class `회원 가입 요청을 받았을 때` {
        @Test
        fun `회원 가입에 성공할 경우 생성된 유저 정보를 반환 한다`() {
            // given
            val requestUrl = "/api/v1/users"
            val request = UserFixture.기본
            val responseType = object : ParameterizedTypeReference<ApiResponse<SignUpResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(requestUrl, HttpMethod.POST, HttpEntity(request), responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(201)) },
                { assertThat(actual.body?.data?.loginId).isEqualTo("loginId") },
                { assertThat(actual.body?.data?.email).isEqualTo("email@domain.com") },
                { assertThat(actual.body?.data?.birthDay).isEqualTo("2025-01-01") },
                { assertThat(actual.body?.data?.gender).isEqualTo(GenderType.MEN) },
            )
        }

        @Test
        fun `요청 바디에 loginId 가 없다면 400 응답을 반환 한다`() {
            // given
            val requestUrl = "/api/v1/users"
            val fixture = UserFixture.기본
            val request = """
                {
                    "email": "${fixture.email}",
                    "birthDay": "${fixture.birthDay}",
                    "gender": "${fixture.gender}"
                }
            """.trimIndent()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(request, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<SignUpResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).isEqualTo("필수 필드 'loginId'이(가) 누락되었습니다.") },
            )
        }

        @Test
        fun `요청 바디에 email 이 없다면 400 응답을 반환 한다`() {
            // given
            val requestUrl = "/api/v1/users"
            val fixture = UserFixture.기본
            val request = """
                {
                    "loginId": "${fixture.loginId}",
                    "birthDay": "${fixture.birthDay}",
                    "gender": "${fixture.gender}"
                }
            """.trimIndent()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(request, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<SignUpResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).isEqualTo("필수 필드 'email'이(가) 누락되었습니다.") },
            )
        }

        @Test
        fun `요청 바디에 birthDay 가 없다면 400 응답을 반환 한다`() {
            // given
            val requestUrl = "/api/v1/users"
            val fixture = UserFixture.기본
            val request = """
                {
                    "loginId": "${fixture.loginId}",
                    "email": "${fixture.email}",
                    "gender": "${fixture.gender}"
                }
            """.trimIndent()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(request, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<SignUpResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).isEqualTo("필수 필드 'birthDay'이(가) 누락되었습니다.") },
            )
        }

        @Test
        fun `요청 바디에 gender 가 없다면 400 응답을 반환 한다`() {
            // given
            val requestUrl = "/api/v1/users"
            val fixture = UserFixture.기본
            val request = """
                {
                    "loginId": "${fixture.loginId}",
                    "email": "${fixture.email}",
                    "birthDay": "${fixture.birthDay}"
                }
            """.trimIndent()

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(request, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<SignUpResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).isEqualTo("필수 필드 'gender'이(가) 누락되었습니다.") },
            )
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "abcde",
                "abcdefghijk",
                "abc-def",
                "abc def",
                "한글아이디",
                "",
            ],
        )
        fun `loginId 가 잘못된 형식이면 400 응답을 반환 한다`(invalidLoginId: String) {
            // given
            val requestUrl = "/api/v1/users"
            val fixture = UserFixture.`6자 미만 로그인 ID`

            val request = SignUpRequest(invalidLoginId, fixture.email, fixture.birthDay, fixture.gender)
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(request, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<SignUpResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).contains("로그인 ID는 6자 이상 10자 이내의 영문 및 숫자만 허용됩니다.") },
            )
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "email",
                "email@",
                "email@domain",
                "email@domain.",
                "email@.",
                "email@.com",
                "domain.com",
                "@domain.com",
            ],
        )
        fun `email 이 잘못된 형식이면 400 응답을 반환 한다`(invalidEmail: String) {
            // given
            val requestUrl = "/api/v1/users"
            val fixture = UserFixture.기본

            val request = SignUpRequest(fixture.loginId, invalidEmail, fixture.birthDay, fixture.gender)
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(request, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<SignUpResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).contains("이메일 형식이 올바르지 않습니다.") },
            )
        }

        @Test
        fun `birthDay 가 잘못된 형식이면 400 응답을 반환 한다`() {
            // given
            val requestUrl = "/api/v1/users"
            val fixture = UserFixture.기본

            val request = SignUpRequest(fixture.loginId, fixture.email, UserFixture.`잘못된 형식의 생년월일`.birthDay, fixture.gender)
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(request, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<SignUpResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).contains("생년월일 형식은 'yyyy-MM-dd' 형식이어야 합니다.") },
            )
        }
    }

    @Nested
    inner class `내 정보 조회 요청을 받았을 때` {
        @Test
        fun `정보 조회에 성공한 경우 해당 유저 정보를 응답으로 반환 한다`() {
            // given
            val requestUrl = "/api/v1/users/me"
            val fixture = UserFixture.기본
            userFacade.createUser(UserCreateCommand(fixture.loginId, fixture.email, fixture.birthDay, fixture.gender))

            val headers = HttpHeaders()
            headers["X-USER-ID"] = fixture.loginId
            val responseType = object : ParameterizedTypeReference<ApiResponse<MyDetailResponse>>() {}

            // when
            val actual = testRestTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity<Any>(headers), responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(200)) },
                { assertThat(actual.body?.data?.loginId).isEqualTo("loginId") },
                { assertThat(actual.body?.data?.email).isEqualTo("email@domain.com") },
                { assertThat(actual.body?.data?.birthDay).isEqualTo("2025-01-01") },
                { assertThat(actual.body?.data?.gender).isEqualTo(GenderType.MEN) },
            )
        }

        @Test
        fun `요청 헤더에 X-USER-ID 헤더가 없는 경우 400 응답을 반환 한다`() {
            // given
            val requestUrl = "/api/v1/users/me"
            val fixture = UserFixture.기본
            userFacade.createUser(UserCreateCommand(fixture.loginId, fixture.email, fixture.birthDay, fixture.gender))

            val responseType = object : ParameterizedTypeReference<ApiResponse<MyDetailResponse>>() {}

            // when
            val actual =
                testRestTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity<Any>(HttpHeaders()), responseType)

            // then
            assertAll(
                { assertThat(actual.statusCode).isEqualTo(HttpStatusCode.valueOf(400)) },
                { assertThat(actual.body?.meta?.message).isEqualTo("loginId 가 누락되었습니다.") },
            )
        }
    }
}
