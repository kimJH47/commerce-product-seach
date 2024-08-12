package back.ecommerce.product.dto.response.v2

import back.ecommerce.product.entity.Category

data class ProductDetailDto(
    val id: Long,
    val name: String,
    val brandName: String,
    val price: Long,
    val category: Category,
    val imageUrls: List<String>,
    val catalogUrl: String,
)