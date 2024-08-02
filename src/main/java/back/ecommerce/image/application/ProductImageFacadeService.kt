package back.ecommerce.image.application

import back.ecommerce.auth.service.AuthService
import back.ecommerce.common.generator.ULIDGenerator
import back.ecommerce.image.dto.request.ImageUploadRequest
import back.ecommerce.image.dto.response.ImageUploadResponse
import back.ecommerce.image.infra.S3UploadImageSpec
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ProductImageFacadeService(
    private val imageStorageService: ImageStorageService,
    private val productImageService: ProductImageService,
    private val imageValidator: ImageValidator,
    private val ulidGenerator: ULIDGenerator,
    private val authService: AuthService
) {

    fun uploads(email: String, request: ImageUploadRequest): ImageUploadResponse {
        val ownerId = authService.findIdByEmail(email)
        val imageId = ulidGenerator.generateULIDToUUID()

        val thumbnailUrl = request.thumbnail?.let { upload(ownerId, imageId, "thumbnail", it) } ?: ""
        val catalogUrl = request.catalog?.let { upload(ownerId, imageId, "catalog", it) } ?: ""

        var imageUrls = ""
        if (request.images.isNotEmpty()) {
            imageUrls = uploadImages(request.images, ownerId, imageId)
        }

        return ImageUploadResponse(productImageService.save(ownerId, imageId, thumbnailUrl, imageUrls, catalogUrl))
    }

    private fun uploadImages(images: List<MultipartFile>, ownerId: Long, imageId: UUID): String {
        var imageNumber = 1
        val urls = mutableListOf<String>()
        images.forEach {
            urls.add(upload(ownerId, imageId, "image-$imageNumber", it))
            imageNumber++
        }
        return urls.joinToString(",")
    }

    fun upload(ownerId: Long, imageId: UUID, fileName: String, image: MultipartFile): String {
        imageValidator.validateType(image)
        val parsePath = parsePath(ownerId, imageId, fileName)
        return imageStorageService.upload(S3UploadImageSpec(parsePath, image))
    }

    /**
     * 파일경로 : /{OwnerId}/{imageId}/{fileName}
     */
    private fun parsePath(ownerId: Long, imageId: UUID, fileName: String): String {
        return "$ownerId/$imageId/$fileName"
    }
}