package back.ecommerce.auth.service

import back.ecommerce.exception.AuthenticationException
import back.ecommerce.exception.ErrorCode
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TokenExtractor(
    @Value("\${jwt.secretKey}")
    private val secretKey: String,
) {
    private val parser: JwtParser = Jwts.parser()

    fun extractClaim(token: String, claimName: String): String {
        return kotlin.runCatching {
            parser.setSigningKey(secretKey)
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

    fun validateAndGetPayload(token: String): Map<String, Any> {
        return kotlin.runCatching {
            parser.setSigningKey(secretKey)
                .parseClaimsJws(token)
                .body
                .toMap()
        }.getAndNullDefaultOrElse(emptyMap()) {
            when (it) {
                is SignatureException -> throw AuthenticationException(ErrorCode.TOKEN_HAS_INVALID)
                is UnsupportedJwtException -> throw AuthenticationException(ErrorCode.TOKEN_HAS_INVALID)
                is MalformedJwtException -> throw AuthenticationException(ErrorCode.TOKEN_HAS_INVALID)
                is ExpiredJwtException -> throw AuthenticationException(ErrorCode.TOKEN_HAS_EXPIRED)
                else -> throw AuthenticationException(ErrorCode.TOKEN_IS_EMPTY)
            }
        }
    }
}

private inline fun <R, T : R> Result<T>.getAndNullDefaultOrElse(default: T, onFailure: (exception: Throwable) -> R): R {
    return this.getOrElse { throwable -> onFailure(throwable) } ?: default
}