package back.ecommerce.image.application

interface ImageStorageService {
    fun upload(spec : ImageUploadSpec) : String
}
