package back.ecommerce.product.dto.response.v2

data class ProductDetailDto(
    val id: Long,
    val name: String,
    val brandName: String,
    val price: Long,
    val imageUrls: List<String>,
    val catalogUrl: String,
)