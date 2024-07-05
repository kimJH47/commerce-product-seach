package back.ecommerce.cart.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class DeleteCartRequest(
    @field:NotBlank(message = "이메일은 필수적으로 필요합니다.")
    @field:Email(message = "옳바른 이메일 형식이 아닙니다.")
    val email: String,
    @field:NotNull(message = "카트 상품의 아이디는 필수적으로 필요합니다.")
    val cartId: Long,
) {
}