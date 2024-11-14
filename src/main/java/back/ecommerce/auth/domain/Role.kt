package back.ecommerce.auth.domain

enum class Role(
    val description: String
) {
    MEMBER("일반 회원"),
    ADMIN("어드민")
}