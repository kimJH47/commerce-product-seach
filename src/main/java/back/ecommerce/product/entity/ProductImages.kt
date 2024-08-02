package back.ecommerce.product.entity

import back.ecommerce.common.entity.ULIDPrimaryKeyEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "PRODUCT_IMAGES")
class ProductImages(
    id: UUID,
    thumbnailUrl: String,
    imageUrls: String,
    catalogUrl: String,
    uploadStatus: UploadStatus,
    productId: Long?,
    ownerId: Long,
    createdDate: LocalDateTime,
    modifiedDate: LocalDateTime?,
) : ULIDPrimaryKeyEntity(id) {

    var thumbnailUrl = thumbnailUrl
        protected set

    var imageUrls = imageUrls
        protected set

    var catalogUrl = catalogUrl
        protected set

    @Enumerated(EnumType.STRING)
    var uploadStatus = uploadStatus
        protected set

    var productId = productId
        protected set

    var ownerId = ownerId
        protected set

    @CreatedDate
    var createdDate: LocalDateTime = createdDate
        protected set

    @LastModifiedDate
    var modifiedDate: LocalDateTime? = modifiedDate
        protected set

    companion object {
        fun createUploaded(
            id: UUID,
            thumbnail: String,
            imageUrls: String,
            catalogUrl: String,
            ownerId: Long,
            createdDate: LocalDateTime
        ): ProductImages {
            return ProductImages(
                id, thumbnail, imageUrls, catalogUrl, UploadStatus.UPLOADED, null, ownerId,
                LocalDateTime.now(), null
            )
        }
    }
}