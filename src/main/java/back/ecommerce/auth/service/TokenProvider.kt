package back.ecommerce.auth.service

import back.ecommerce.auth.dto.response.Token
import back.ecommerce.exception.AuthenticationException
import back.ecommerce.exception.ErrorCode
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenProvider(
    @Value("\${jwt.secretKey}")
    private val securityKey: String
) {

    private val parser: JwtParser = Jwts.parser()

    fun provide(email: String, expiredTime: Int): Token {
        val payload = HashMap<String, Any>()
        payload["email"] = email

        val token = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg", SIGNATURE_ALGORITHM)
            .setClaims(payload)
            .setExpiration(createExpireTime(expiredTime))
            .signWith(SignatureAlgorithm.HS256, securityKey)
            .compact()

        return Token(token, expiredTime, TYPE)
    }

    private fun createExpireTime(expireTime: Int): Date {
        val expireDate = Date()
        expireDate.time += expireTime
        return expireDate
    }

    fun extractClaim(token: String?, claimName: String): String {
        return kotlin.runCatching {
            parser.setSigningKey(securityKey)
                .parseClaimsJws(token)
                .body
                .get(claimName, String::class.java)
        }.getAndNullDefaultOrElse("") {
            when (it) {
                is SignatureException -> throw AuthenticationException(ErrorCode.TOKEN_HAS_INVALID)
                is UnsupportedJwtException -> throw AuthenticationException(ErrorCode.TOKEN_HAS_INVALID)
                is MalformedJwtException -> throw AuthenticationException(ErrorCode.TOKEN_HAS_INVALID)
                is ExpiredJwtException -> throw AuthenticationException(ErrorCode.TOKEN_HAS_EXPIRED)
                else -> throw AuthenticationException(ErrorCode.TOKEN_IS_EMPTY)
            }
        }
    }

    companion object {
        private const val TYPE = "Bearer"
        private val SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256
    }
}

inline fun <R, T : R> Result<T>.getAndNullDefaultOrElse(default: T, onFailure: (exception: Throwable) -> R): R {
    return this.getOrElse { throwable -> onFailure(throwable) } ?: default
}
