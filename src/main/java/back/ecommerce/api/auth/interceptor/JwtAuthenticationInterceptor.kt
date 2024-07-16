package back.ecommerce.api.auth.interceptor

import back.ecommerce.auth.service.TokenExtractor
import back.ecommerce.exception.AuthenticationException
import back.ecommerce.exception.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
@RequiredArgsConstructor
class JwtAuthenticationInterceptor(
    private val tokenExtractor: TokenExtractor
) : HandlerInterceptor {

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        request.getHeader(HttpHeaders.AUTHORIZATION)?.let {
            if (it.startsWith(AUTHORIZATION_TYPE)) {
                it.removePrefix(AUTHORIZATION_TYPE)
            } else throw AuthenticationException(ErrorCode.AUTH_HEADER_INVALID)
            tokenExtractor.extractClaim(it, EMAIL_ATTRIBUTE)
        }.also {
            request.setAttribute(EMAIL_ATTRIBUTE, it)
        } ?: throw AuthenticationException(ErrorCode.AUTH_HEADER_IS_EMPTY)

        return true
    }

    companion object {
        private const val AUTHORIZATION_TYPE = "Bearer "
        private const val EMAIL_ATTRIBUTE = "email"
    }
}
