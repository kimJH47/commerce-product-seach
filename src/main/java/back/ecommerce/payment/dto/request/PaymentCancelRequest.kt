package back.ecommerce.payment.dto.request

import jakarta.validation.constraints.NotNull

data class PaymentCancelRequest(
    @field:NotNull(message = "주문번호는 필수적으로 필요합니다.")
    val orderCode: String
)