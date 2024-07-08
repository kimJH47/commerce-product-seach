package back.ecommerce.auth.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class SignUpResponse(
    val email: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val requestTime: LocalDateTime
)