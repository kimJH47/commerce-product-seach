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
        validateEmail(email).also {
            signUpRepository.save(SignUpInfo.create(code, email, password, EMAIL_TOKEN_EXPIRED_SECONDS))
        }
    }

    @Transactional
    fun verifyCodeAndSaveUser(code: String): String {
        val signUpInfo = signUpRepository.findByVerifiedCode(code)
            .orElseThrow { CustomException(ErrorCode.EMAIL_CODE_NOT_FOUND) }.also {
                validate(it)
            }

        userRepository.save(User.create(signUpInfo.email, signUpInfo.password))
        return signUpInfo.email
    }

    private fun validate(signUpInfo: SignUpInfo) {
        validateEmail(signUpInfo.email)
        if (signUpInfo.isExpired) {
            throw CustomException(ErrorCode.TOKEN_HAS_EXPIRED)
        }
    }

    private fun validateEmail(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw CustomException(ErrorCode.DUPLICATE_USER_EMAIL)
        }
    }

    companion object {
        private const val EMAIL_TOKEN_EXPIRED_SECONDS = (60 * 15).toLong()
    }
}
