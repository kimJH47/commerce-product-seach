package back.ecommerce.image.infra

import back.ecommerce.image.application.ImageUploadSpec
import org.springframework.web.multipart.MultipartFile

data class S3UploadImageSpec(
    val imagePath: String,
    val image: MultipartFile
) : ImageUploadSpec
