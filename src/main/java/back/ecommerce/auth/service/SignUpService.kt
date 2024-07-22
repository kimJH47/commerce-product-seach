package back.ecommerce.auth.service

import back.ecommerce.exception.CustomException
import back.ecommerce.exception.ErrorCode
import back.ecommerce.user.entity.SignUpInfo
import back.ecommerce.user.entity.User
import back.ecommerce.user.repository.SignUpRepository
import back.ecommerce.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpService(
    private val userRepository: UserRepository,
    private val signUpRepository: SignUpRepository
) {

    @Transactional
    fun saveUserSignUpInfo(code: String, email: String, password: String) {
        validateDuplicateEmail(email)
        validateAlreadySignUpRequest(email).also {
            signUpRepository.save(SignUpInfo.create(code, email, password, EMAIL_TOKEN_EXPIRED_SECONDS))
        }
    }

    @Transactional
    fun verifyCodeAndSaveUser(code: String): String {
        val signUpInfo = signUpRepository.findByVerifiedCode(code)
            .orElseThrow { CustomException(ErrorCode.EMAIL_CODE_NOT_FOUND) }.also {
                if (it.isExpired) {
                    throw CustomException(ErrorCode.TOKEN_HAS_EXPIRED)
                }
            }
        validateDuplicateEmail(signUpInfo.email)
        userRepository.save(User.create(signUpInfo.email, signUpInfo.password))
        signUpRepository.deleteById(signUpInfo.id)
        return signUpInfo.email
    }

    private fun validateAlreadySignUpRequest(email: String) {
        if (signUpRepository.existsByEmail(email)) {
            throw CustomException(ErrorCode.ALREADY_SIGN_UP_EMAIL)
        }
    }

    private fun validateDuplicateEmail(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw CustomException(ErrorCode.DUPLICATE_USER_EMAIL)
        }
    }

    companion object {
        private const val EMAIL_TOKEN_EXPIRED_SECONDS = (60 * 15).toLong() //15분
    }
}
