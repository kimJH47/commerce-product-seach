package back.ecommerce.image.infra

import back.ecommerce.image.application.ImageStorageProcessor
import back.ecommerce.image.application.ImageUploadSpec
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class S3ImageStorageService(
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket}") val bucket: String,
) : ImageStorageProcessor {

    override fun upload(spec: ImageUploadSpec): String {
        if (spec !is S3UploadImageSpec) {
            throw Exception("${spec::class.simpleName} is not valid. Only S3UploadImageSpec instances are allowed.")
        }
        val metadata = ObjectMetadata().apply {
            contentType = spec.file.contentType
            contentLength = spec.file.size
        }
        try {
            amazonS3Client.putObject(bucket, spec.filePath, spec.file.inputStream, metadata)
        } catch (e: Exception) {
            throw Exception("Error uploading image", e)
        }
        return amazonS3Client.getUrl(bucket, spec.filePath).toString()
    }
}