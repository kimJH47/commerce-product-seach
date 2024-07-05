package back.ecommerce.admin.dto.response

import back.ecommerce.product.entity.ApprovalStatus
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class AddRequestProductResponse(
    val email: String,
    val requestId: Long,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val requestTime: LocalDateTime,
    val approvalStatus: ApprovalStatus
)