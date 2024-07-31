package back.ecommerce.image.application

interface ImageStorageProcessor {
    fun upload(spec : ImageUploadSpec) : String
}
