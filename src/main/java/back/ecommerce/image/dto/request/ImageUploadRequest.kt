package back.ecommerce.image.dto.request

import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile

data class ImageUploadRequest(
    val thumbnail: MultipartFile? = null,
    val catalog: MultipartFile? = null,
    @field:Size(max = 5, message = "상품 이미지의 최대 갯수는 5개 입니다.")
    val images: List<MultipartFile> = emptyList()
) {
    fun toMap(): Map<String, Any> = buildMap {
        thumbnail?.let { put("thumbnail", it) }
        catalog?.let { put("catalog", it) }
        if (images.isNotEmpty()) {
            put("images", images)
        }
    }
}

