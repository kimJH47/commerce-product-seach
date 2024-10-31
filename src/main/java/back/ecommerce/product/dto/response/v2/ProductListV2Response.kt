package back.ecommerce.product.dto.response.v2


//thumbnail url 응답추가
data class ProductListV2Response(
    val totalCount: Int,
    val products: List<ProductV2Dto>
)
