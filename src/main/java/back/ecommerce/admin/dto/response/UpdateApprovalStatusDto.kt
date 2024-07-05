package back.ecommerce.admin.dto.response

import back.ecommerce.product.entity.ApprovalStatus

data class UpdateApprovalStatusDto(
    val email: String,
    val requestId: Long,
    val approvalStatus: ApprovalStatus,
) {

    fun toMap(): Map<String, String> {
        return mapOf(
            "email" to email,
            "requestId" to requestId.toString(),
            "approvalStatus" to approvalStatus.toString()
        )
    }
}