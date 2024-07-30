package back.ecommerce.image.dto.request

import org.springframework.web.multipart.MultipartFile

data class ImageUploadRequest(
    val thumbnail: MultipartFile? = null,
    val catalog: MultipartFile? = null,
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

