package back.ecommerce.api.auth.interceptor

import back.ecommerce.admin.repository.AdminRepository
import back.ecommerce.exception.AuthenticationException
import back.ecommerce.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AdminAuthorizationInterceptor(
    private val adminRepository: AdminRepository
) : HandlerInterceptor {

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        val email = request.getAttribute(EMAIL_ATTRIBUTE) as String

        adminRepository.findByEmail(email)
            .orElseThrow { AuthenticationException(ErrorCode.ADMIN_NOT_FOUND) }

        return super.preHandle(request, response, handler)
    }

    companion object {
        private const val EMAIL_ATTRIBUTE = "email"
    }
}
