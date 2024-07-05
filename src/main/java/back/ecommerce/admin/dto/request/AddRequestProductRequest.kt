package back.ecommerce.admin.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AddRequestProductRequest(
    @field:NotBlank(message = "이메일은 필수적으로 필요합니다.")
    @field:Email(message = "옳바른 이메일 형식이 아닙니다.")
    var email: String,
    @field:NotBlank(message = "상품의 이름은 필수적으로 필요 합니다.")
    val name: String,
    @field:NotBlank(message = "브랜드의 이름은 필수적으로 필요 합니다.")
    val brandName: String,
    @field:NotNull(message = "가격정보는 필수적으로 필요 합니다.")
    @field:Min(value = 1, message = "상품의 가격은 최소 1 이상 이여야 합니다.")
    val price: Long?,
    @field:NotBlank(message = "상품의 카테고리는 필수적으로 필요 합니다.")
    val category: String
)