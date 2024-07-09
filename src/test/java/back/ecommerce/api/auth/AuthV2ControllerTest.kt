package back.ecommerce.api.auth

import back.ecommerce.api.spec.ApiTestSpec
import back.ecommerce.api.support.*
import back.ecommerce.auth.dto.request.LoginRequest
import back.ecommerce.auth.dto.request.SignUpRequest
import back.ecommerce.auth.dto.response.SignUpDto
import back.ecommerce.auth.dto.response.SignUpResponse
import back.ecommerce.auth.dto.response.TokenResponse
import back.ecommerce.auth.service.AuthService
import back.ecommerce.publisher.aws.EmailSQSEventPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.ints.exactly
import io.mockk.every
import io.mockk.verify
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime
import java.util.*

class AuthV2ControllerTest(
    mockMvc: MockMvc,
    objectMapper: ObjectMapper,
    authService: AuthService,
    emailSQSEventPublisher: EmailSQSEventPublisher

) : ApiTestSpec(
    {
        describe("/api/v2/auth/token POST") {
            context("유효한 요청이 들어오면") {
                val accessToken = "dsdasdmdsamkdsal.sddsamkldmlsa.dsamlkdmsaml21salkdm"
                val expireTime = 10000L
                val bearer = "Bearer"
                val actual = TokenResponse(accessToken, expireTime, bearer)
                every { authService.createToken(any(String::class), any(String::class)) } returns actual
                val requestBody = LoginRequest("dsldsalw42@email.com", "dsamkcmx#dsm")

                it("상태코드 200과 함게 엑세스 토큰이 응답 되어야 한다.") {
                    mockMvc.docPost("/api/v2/auth/token") {
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(requestBody)
                    }.andExpect { status { isOk() } }
                        .andExpect {
                            jsonPath("$.entity.accessToken", equalTo(accessToken))
                            jsonPath("$.entity.expireTime", equalTo(10000))
                            jsonPath("$.entity.type", equalTo(bearer))
                        }
                        .andDocument("로그인 API") {
                            requestFields("email" type STRING means "이메일", "password" type STRING means "비밀번호")
                            responseFields(
                                "message" type STRING means "응답 메시지",
                                "entity.accessToken" type STRING means "인증용 엑세스 토큰",
                                "entity.expireTime" type NUMBER means "토큰 만료시간",
                                "entity.type" type STRING means "인증헤더 타입"
                            )
                        }
                    verify(exactly = 1) { authService.createToken(any(String::class), any(String::class)) }
                }

            }
        }
        describe("/api/v2/auth/sign-up POST") {
            context("유효한 요청이 들어오면") {

                every { authService.signUp(any(), any()) } returns SignUpDto(
                    "test@test.com",
                    UUID.randomUUID().toString()
                )

                every { emailSQSEventPublisher.pub(any(), any()) } returns Unit

                it("상태코드 200과 함께 가입 이메일이 응답 되어야 한다.") {
                    mockMvc.docPost("/api/v2/auth/sign-up") {
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(SignUpRequest("test@test.com", "pass"))
                    }.andExpect { status { isOk() } }
                        .andExpect {
                            jsonPath("$.message", equalTo("회원가입 요청이 성공적으로 완료되었습니다."))
                            jsonPath("$.entity.email", equalTo("test@test.com"))
                            jsonPath("$.entity.requestTime", notNullValue())
                        }.andDocument("회원가입 API") {
                            requestFields("email" type STRING means "이메일", "password" type STRING means "비밀번호")
                            responseFields(
                                "message" type STRING means "응답 메시지",
                                "entity.email" type STRING means "이메일",
                                "entity.requestTime" type DATETIME means "요청시간"
                            )
                        }
                    verify {
                        exactly(1)
                        authService.signUp(any(), any())
                    }
                    verify {
                        exactly(1)
                        emailSQSEventPublisher.pub(any(), any())
                    }
                }
            }
        }
        describe("/api/v2/auth/verified/{code} GET") {
            val response = SignUpResponse("test@test.com", LocalDateTime.now())
            val code = UUID.randomUUID()
            every { authService.verifyEmailCode(any()) } returns response

            context("유효한 요청이 들어오면") {
                it("상태코드 200과 함께 가입 이메일이 응답 되어야 한다.") {
                    mockMvc.docGet("/api/v2/auth/verified/{code}", code)
                        .andExpect { status { isOk() } }
                        .andExpect {
                            jsonPath("$.message", equalTo("이메일 인증이 성공적으로 완료되었습니다."))
                            jsonPath("$.entity.email", equalTo("test@test.com"))
                            jsonPath("$.entity.requestTime", notNullValue())
                        }.andDocument("이메일 인증 API") {
                            pathParameters("code" to "이메일 인증코드")
                            responseFields(
                                "message" type STRING means "응답 메시지",
                                "entity.email" type STRING means "이메일",
                                "entity.requestTime" type DATETIME means "요청시간"
                            )
                        }

                    verify(exactly = 1) { authService.verifyEmailCode(any()) }
                }
            }
        }
    }
)