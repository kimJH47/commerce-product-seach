package back.ecommerce.product.dto.response.v2

import okhttp3.internal.immutableListOf

//thumbnail url 응답추가
data class ProductListV2Response(
    val totalCount: Int,
    val products: List<ProductV2Dto> = immutableListOf(),
)
