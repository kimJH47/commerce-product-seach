package back.ecommerce.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class VerifyTokenRequest(
    @field:NotBlank(message = "토큰은 필수적으로 필요합니다.")
    val token: String
)