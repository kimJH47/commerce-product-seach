package back.ecommerce.image.infra

import back.ecommerce.image.application.ImageUploadSpec
import org.springframework.web.multipart.MultipartFile

data class S3UploadImageSpec(
    val filePath: String,
    val file: MultipartFile
) : ImageUploadSpec
