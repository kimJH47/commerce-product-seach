package back.ecommerce.image.application

import back.ecommerce.exception.CustomException
import back.ecommerce.exception.ErrorCode.INVALID_IMAGE_CONTENT_TYPE
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class ImageValidator {
    fun validateType(image: MultipartFile) {
        if (!imageTypes.contains(image.contentType)) {
            throw CustomException(INVALID_IMAGE_CONTENT_TYPE)
        }
    }

    companion object {
        val imageTypes = arrayOf("image/jpg", "image/jpeg", "image/png")
    }
}
