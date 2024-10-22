package back.ecommerce.product.dto.response.v2

import back.ecommerce.product.entity.Category
import com.querydsl.core.annotations.QueryProjection

data class ProductV2Dto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val brandName: String,
    val price: Long,
    val category: Category,
    val thumbnailUrl: String
)
