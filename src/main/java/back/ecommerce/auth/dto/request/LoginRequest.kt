package back.ecommerce.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "이메일은 필수적으로 필요합니다.") @Email(message = "옳바른 이메일 형식이 아닙니다.")
    val email: String,
    @field:NotBlank(message = "비밀번호는 필수적으로 필요합니다.")
    val password: String
)