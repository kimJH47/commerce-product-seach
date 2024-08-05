package back.ecommerce.auth.interceptor

import back.ecommerce.api.auth.interceptor.JwtAuthenticationInterceptor
import back.ecommerce.auth.service.TokenExtractor
import back.ecommerce.exception.AuthenticationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

internal class JwtAuthenticationInterceptorTest : DescribeSpec(
    {
        isolationMode = IsolationMode.InstancePerTest

        val tokenExtractor: TokenExtractor = mockk<TokenExtractor>()
        val request: HttpServletRequest = mockk<HttpServletRequest>()
        val response: HttpServletResponse = mockk<HttpServletResponse>()
        val handler = ArrayList<Any>()
        val jwtAuthenticationInterceptor = JwtAuthenticationInterceptor(tokenExtractor)

        describe("토큰 인터셉터 테스트") {
            context("유효한 토큰이 헤더에 담겨있을 때") {
                val header = ("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
                        + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
                        + ".3UsoYUK0_vvq2jwN6eskqTAC2E9xStyN7iVwZ7d3rw4")
                val token =
                    ("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
                            + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
                            + ".3UsoYUK0_vvq2jwN6eskqTAC2E9xStyN7iVwZ7d3rw4")

                every { request.getHeader(any()) } returns header
                every { tokenExtractor.extractClaim(any(), any()) } returns token
                every { request.setAttribute(any(), any()) } returns Unit

                it("true 를 반환해야한다.") {
                    jwtAuthenticationInterceptor.preHandle(request, response, handler) shouldBe true
                    verify(exactly = 1) { request.getHeader(any()) }
                    verify(exactly = 1) { tokenExtractor.extractClaim(any(), any()) }
                    verify(exactly = 1) { request.setAttribute(any(), any()) }
                }

            }
            context("Authorization 헤더에 토큰이 존재 하지 않으면") {
                val header = null
                every { request.getHeader(any()) } returns header
                every { request.setAttribute(any(), any()) } returns Unit

                it("예외가 발생한다.") {

                    shouldThrow<AuthenticationException> {
                        jwtAuthenticationInterceptor.preHandle(request, response, handler)
                    }.message shouldBe "토큰이 비어있습니다."

                    verify(exactly = 1) { request.getHeader(any()) }
                    verify(exactly = 0) { tokenExtractor.extractClaim(any(), any()) }
                    verify(exactly = 0) { request.setAttribute(any(), any()) }
                }
            }

            context("Authorization 헤더에 인증타입이 Bearer 가 아니면") {
                val header = ("Basic eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
                        + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
                        + ".3UsoYUK0_vvq2jwN6eskqTAC2E9xStyN7iVwZ7d3rw4")

                every { request.getHeader(any()) } returns header
                every { request.setAttribute(any(), any()) } returns Unit

                it("예외가 발생한다.") {

                    shouldThrow<AuthenticationException> {
                        jwtAuthenticationInterceptor.preHandle(request, response, handler)
                    }.message shouldBe "인증 헤더타입이 일치하지 않습니다."

                    verify(exactly = 1) { request.getHeader(any()) }
                    verify(exactly = 0) { tokenExtractor.extractClaim(any(), any()) }
                    verify(exactly = 0) { request.setAttribute(any(), any()) }
                }
            }

        }
    }
)