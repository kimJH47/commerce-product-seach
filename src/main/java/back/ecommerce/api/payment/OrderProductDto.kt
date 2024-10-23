package back.ecommerce.api.payment

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class OrderProductDto(
    @field:NotNull(message = "상품의 아이디는 필수적으로 필요합니다.")
    val productId: Long,
    @field:NotNull(message = "주문상품의 이름은 필수적으로 필요합니다.")
    val name: String,
    @field:Min(value = 1, message = "상품의 갯수는 최소 1개 이상이여야 합니다.")
    val quantity: Int,
    @field:NotNull(message = "가격은 필수적으로 필요 합니다.")
    val price: Long
)

