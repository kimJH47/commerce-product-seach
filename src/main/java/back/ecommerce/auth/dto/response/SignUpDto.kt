package back.ecommerce.auth.dto.response

data class SignUpDto(
    val email: String,
    val code: String
) {

    fun toMap(): Map<String, String> {
        return mapOf("email" to email, "code" to code)
    }
}