package back.ecommerce.api.auth.v2

import back.ecommerce.api.dto.Response
import back.ecommerce.auth.dto.request.LoginRequest
import back.ecommerce.auth.dto.request.SignUpRequest
import back.ecommerce.auth.dto.request.VerifyTokenRequest
import back.ecommerce.auth.dto.response.SignUpResponse
import back.ecommerce.auth.service.AuthService
import back.ecommerce.publisher.aws.EmailPublisher
import back.ecommerce.publisher.aws.MessageType
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v2/auth")
class AuthV2Controller(
    private val authService: AuthService,
    private val emailPublisher: EmailPublisher
) {

    @PostMapping("/token")
    fun login(@RequestBody @Valid request: LoginRequest): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "인증이 성공적으로 완료되었습니다",
            authService.createToken(request.email, request.password)
        )
    }

    @PostMapping("/sign-up")
    fun signUp(@RequestBody @Valid request: SignUpRequest): ResponseEntity<Response> {
        val signUp = authService.signUp(request.email, request.password)
        emailPublisher.pub(MessageType.VERIFIED_CODE, signUp.toMap())

        return Response.createSuccessResponse(
            "회원가입 요청이 성공적으로 완료되었습니다.",
            SignUpResponse(signUp.email, LocalDateTime.now())
        )
    }

    @GetMapping("/verified/{code}")
    fun verifyEmailCode(@PathVariable("code") code: String): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "이메일 인증이 성공적으로 완료되었습니다.",
            authService.verifyEmailCode(code)
        )
    }

    @PostMapping("/verify-token")
    fun verifyToken(request: VerifyTokenRequest): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "토큰검증이 완료 되었습니다.",
            Pair("isVerify", authService.verifyToken(request.token))
        )
    }
}