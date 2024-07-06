package back.ecommerce.payment.dto.request

import back.ecommerce.api.payment.OrderProductDto
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class PaymentReadyRequest(
    @field:NotBlank(message = "이메일은 필수적으로 필요합니다.")
    @field:Email(message = "옳바른 이메일 형식이 아닙니다.")
    val email: String,
    @field:Min(value = 1, message = "상품의 전체갯수는 최소 1개 이상이여야 합니다.")
    val totalCount: Long,
    @field:Min(value = 1, message = "상품의 전체금액은 최소 1 이상이여야 합니다.")
    val totalPrice: Long,
    var orderProducts: List<OrderProductDto>
)