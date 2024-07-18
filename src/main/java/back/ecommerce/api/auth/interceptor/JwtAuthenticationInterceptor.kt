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

        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: throw AuthenticationException(ErrorCode.TOKEN_IS_EMPTY)
        if (!header.startsWith(AUTHORIZATION_TYPE)) {
            throw AuthenticationException(ErrorCode.AUTH_HEADER_INVALID)
        }
        header.removePrefix(AUTHORIZATION_TYPE)
        tokenExtractor.extractClaim(header, EMAIL_ATTRIBUTE).also {
            request.setAttribute(EMAIL_ATTRIBUTE, it)
        }
        return true
    }

    companion object {
        private const val AUTHORIZATION_TYPE = "Bearer "
        private const val EMAIL_ATTRIBUTE = "email"
    }
}
