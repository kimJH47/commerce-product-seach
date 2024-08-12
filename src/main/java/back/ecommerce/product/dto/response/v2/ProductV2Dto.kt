package back.ecommerce.product.dto.response.v2

import back.ecommerce.product.entity.Category

/**
썸네일 url 추가
 */
data class ProductV2Dto(
    val id: Long,
    val name: String,
    val brandName: String,
    val price: Long,
    val category: Category,
    val thumbnail: String
)
