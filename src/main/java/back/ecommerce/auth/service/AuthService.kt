package back.ecommerce.auth.service

import back.ecommerce.auth.dto.response.SignUpDto
import back.ecommerce.auth.dto.response.SignUpResponse
import back.ecommerce.auth.dto.response.TokenResponse
import back.ecommerce.auth.token.TokenProvider
import back.ecommerce.common.generator.RandomUUIDGenerator
import back.ecommerce.exception.AuthenticationException
import back.ecommerce.exception.CustomException
import back.ecommerce.exception.ErrorCode
import back.ecommerce.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val randomUUIDGenerator: RandomUUIDGenerator,
    private val signUpService: SignUpService,
    private val verificationURLGenerator: VerificationURLGenerator,
    @Value("\${jwt.expiredTime}") private val expiredTime: Int
) {
    @Transactional(readOnly = true)
    fun createToken(email: String, password: String): TokenResponse {
        val user = userRepository.findByEmail(email)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }
        validatePassword(password, user.password)
        val token = tokenProvider.provide(email, expiredTime)
        return TokenResponse(token.value, token.expireTime.toLong(), token.type)
    }

    private fun validatePassword(rawPassword: String, encodePassword: String) {
        passwordEncoder.matches(
            rawPassword, encodePassword
        ) || throw AuthenticationException(ErrorCode.PASSWORD_NOT_MATCHED)
    }

    fun signUp(email: String, password: String): SignUpDto {
        val code = randomUUIDGenerator.create()
        signUpService.saveUserSignUpInfo(code, email, password)
        return SignUpDto(email, verificationURLGenerator.generateVerificationURL(code))
    }

    fun verifyEmailCode(code: String): SignUpResponse {
        val email = signUpService.verifyCodeAndSaveUser(code)
        return SignUpResponse(email, LocalDateTime.now())
    }
}
