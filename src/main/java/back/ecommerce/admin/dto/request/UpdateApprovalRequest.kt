package back.ecommerce.admin.dto.request

import back.ecommerce.product.entity.ApprovalStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateApprovalRequest(
    @field:NotNull(message = "등록요청 상품 아아디는 필수적으로 필요합니다.")
    var requestId: Long,
    @field:NotNull(message = "승인상태는 필수적로 필요합니다.")
    val approvalStatus: ApprovalStatus,
    @field:NotBlank(message = "이메일은 필수적으로 필요합니다.")
    @field:Email(message = "옳바른 이메일 형식이 아닙니다.")
    val email: String
)