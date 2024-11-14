package back.ecommerce.auth.service

import back.ecommerce.auth.domain.AuthUser
import back.ecommerce.auth.domain.JwtAuthentication
import back.ecommerce.auth.domain.Role
import org.springframework.stereotype.Service

@Service
class JwtAuthenticationProvider {
    fun provide(payload: Map<String, Any>): JwtAuthentication {
        val nickname = payload[NICKNAME_PAYLOAD] as String
        val authUser = AuthUser(nickname, Role.MEMBER)
        return JwtAuthentication(authUser, true)
    }

    companion object {
        private const val NICKNAME_PAYLOAD = "nickname"
    }
}
