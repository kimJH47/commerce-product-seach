package back.ecommerce.auth.service

import back.ecommerce.auth.dto.response.Token
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenProvider(
    @Value("\${jwt.secretKey}")
    private val securityKey: String
) {
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

    companion object {
        private const val TYPE = "Bearer"
        private val SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256
    }
}

