package back.ecommerce.admin.dto.response

import back.ecommerce.product.entity.ApprovalStatus
import back.ecommerce.product.entity.Category
import back.ecommerce.product.entity.RequestProduct
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class RequestProductDto(
    val requestId: Long,
    val email: String,
    val name: String,
    val brandName: String,
    val category: Category,
    val price: Long,
    val approvalStatus: ApprovalStatus,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val requestTime: LocalDateTime
) {
    constructor(requestProduct: RequestProduct) : this(
        requestProduct.id,
        requestProduct.email,
        requestProduct.name,
        requestProduct.brandName,
        requestProduct.category,
        requestProduct.price,
        requestProduct.approvalStatus,
        requestProduct.createdDate,
    )

    companion object {
        fun create(requestProduct: RequestProduct) = RequestProductDto(requestProduct)
    }
}