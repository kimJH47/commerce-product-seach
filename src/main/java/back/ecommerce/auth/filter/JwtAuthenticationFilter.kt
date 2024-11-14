package back.ecommerce.auth.filter

import back.ecommerce.auth.service.JwtAuthenticationProvider
import back.ecommerce.auth.service.TokenExtractor
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenExtractor: TokenExtractor,
    private val authenticationProvider: JwtAuthenticationProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header == null || !header.startsWith(AUTHORIZATION_TYPE)) {
            filterChain.doFilter(request, response)
            return
        }

        val payload = tokenExtractor.validateAndGetPayload(header.removePrefix(AUTHORIZATION_TYPE))
        val authentication = authenticationProvider.provide(payload)

        SecurityContextHolder.getContext()?.run {
            this.authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        private const val AUTHORIZATION_TYPE = "Bearer "
    }
}