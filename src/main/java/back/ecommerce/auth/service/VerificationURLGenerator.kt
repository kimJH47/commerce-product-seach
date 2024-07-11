package back.ecommerce.auth.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class VerificationURLGenerator(
    @Value("\${host.url}") val hostUrl: String
) {
    fun generateVerificationURL(code: String): String {
        return "$hostUrl/api/v2/auth/verified/$code"
    }
}