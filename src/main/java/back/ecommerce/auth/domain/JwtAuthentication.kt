package back.ecommerce.auth.domain

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class JwtAuthentication(
    private val authUser: AuthUser,
    private var isAuthenticated: Boolean = false
) : Authentication {

    override fun getName(): String {
        return authUser.username
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authUser.authorities
    }

    override fun getCredentials(): Any {
        return authUser
    }

    override fun getDetails(): Any {
        return authUser
    }

    override fun getPrincipal(): Any {
        return authUser
    }

    override fun isAuthenticated(): Boolean {
        return isAuthenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.isAuthenticated = isAuthenticated
    }
}