package com.loopers.presentation.auth

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer

class LoginIdArgumentResolverTest {

    class MethodFixture {
        fun 애너테이션_없는_문자열_파라미터_메서드(문자열_파라미터: String) = Unit
        fun 문자열_아닌_파라미터_메서드(문자열_아닌_파라미터: Int) = Unit
        fun 애너테이션_있지만_문자열이_아닌_파라미터_메서드(@LoginId 애너테이션_파라미터: Int) = Unit
        fun 애너테이션_있는_문자열_파라미터_메서드(@LoginId 애너테이션_문자열_파라미터: String) = Unit
    }

    @Nested
    inner class `메서드 파라미터가 X-User-ID 를 주입 받는 파라미터인지 검증할 때` {
        private val methodFixtures = MethodFixture::class.java

        @Test
        fun `파라미터가 UserId 애너테이션이 없다면 false 를 반환 한다`() {
            // given
            val cut = UserIdArgumentResolver()
            val methodFixture = methodFixtures.getDeclaredMethod("애너테이션_없는_문자열_파라미터_메서드", String::class.java)
            val parameter = MethodParameter(methodFixture, 0)

            // when
            val actual = cut.supportsParameter(parameter)

            // then
            assertThat(actual).isFalse
        }

        @Test
        fun `파라미터가 UserId 문자열이 아니라면 false 를 반환 한다`() {
            // given
            val cut = UserIdArgumentResolver()
            val methodFixture = methodFixtures.getDeclaredMethod("문자열_아닌_파라미터_메서드", Int::class.java)
            val parameter = MethodParameter(methodFixture, 0)

            // when
            val actual = cut.supportsParameter(parameter)

            // then
            assertThat(actual).isFalse
        }

        @Test
        fun `파라미터에 UserId 애너테이션이 있지만 문자열이 아니라면 false 를 반환 한다`() {
            // given
            val cut = UserIdArgumentResolver()
            val methodFixture = methodFixtures.getDeclaredMethod("애너테이션_있지만_문자열이_아닌_파라미터_메서드", Int::class.java)
            val parameter = MethodParameter(methodFixture, 0)

            // when
            val actual = cut.supportsParameter(parameter)

            // then
            assertThat(actual).isFalse
        }

        @Test
        fun `파라미터가 UserId 애너테이션이 있는 문자열이라면 true 를 반환 한다`() {
            // given
            val cut = UserIdArgumentResolver()
            val methodFixture = methodFixtures.getDeclaredMethod("애너테이션_있는_문자열_파라미터_메서드", String::class.java)
            val parameter = MethodParameter(methodFixture, 0)

            // when
            val actual = cut.supportsParameter(parameter)

            // then
            assertThat(actual).isTrue
        }
    }

    @Nested
    inner class `HTTP 요청에서 X-USER-ID 헤더를 핸들러 파라미터로 매핑할 때` {
        @Test
        fun `헤더에 X-USER-ID 가 없다면 CoreException REQUIRED_LOGIN_ID_HEADER 예외를 던진다`() {
            // given
            val cut = UserIdArgumentResolver()

            val parameter: MethodParameter = mockk()
            val mavContainer: ModelAndViewContainer = mockk()
            val webRequest: NativeWebRequest = mockk()
            val binderFactory: WebDataBinderFactory = mockk()

            every { webRequest.getHeader("X-USER-ID") } returns null

            // when then
            assertThatThrownBy {
                cut.resolveArgument(parameter, mavContainer, webRequest, binderFactory)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(ErrorType.REQUIRED_LOGIN_ID_HEADER, "loginId 가 누락되었습니다.")
        }

        @Test
        fun `헤더에 X-USER-ID 가 있다면 반환 한다`() {
            // given
            val cut = UserIdArgumentResolver()

            val parameter: MethodParameter = mockk()
            val mavContainer: ModelAndViewContainer = mockk()
            val webRequest: NativeWebRequest = mockk()
            val binderFactory: WebDataBinderFactory = mockk()

            every { webRequest.getHeader("X-USER-ID") } returns "loginId"

            // when
            val actual = cut.resolveArgument(parameter, mavContainer, webRequest, binderFactory)

            // then
            assertThat(actual).isEqualTo("loginId")
        }
    }
}
