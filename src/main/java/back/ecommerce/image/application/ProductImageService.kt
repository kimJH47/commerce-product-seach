package back.ecommerce.image.application

import back.ecommerce.product.entity.ProductImages
import back.ecommerce.product.repository.ProductImageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class ProductImageService(
    private val productImageRepository: ProductImageRepository
) {

    @Transactional
    fun save(ownerId: Long, imageId: UUID, thumbnail: String, imageUrls: String, catalogUrl: String): UUID {
        val productImages = ProductImages.createUploaded(
            imageId, thumbnail, imageUrls, catalogUrl, ownerId, LocalDateTime.now()
        )
        return productImageRepository.save(productImages).id
    }
}
