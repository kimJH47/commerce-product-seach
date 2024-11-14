package back.ecommerce.auth.service

import back.ecommerce.auth.domain.AuthUser
import back.ecommerce.auth.domain.JwtAuthentication
import back.ecommerce.auth.domain.Role
import org.springframework.stereotype.Service

@Service
class JwtAuthenticationProvider {
    fun provide(payload: Map<String, Any>): JwtAuthentication {
        val email = payload[EMAIL_PAYLOAD_KEY] as String
        val authUser = AuthUser(email, Role.MEMBER)
        return JwtAuthentication(authUser, true)
    }

    companion object {
        private const val EMAIL_PAYLOAD_KEY = "email"
    }
}
