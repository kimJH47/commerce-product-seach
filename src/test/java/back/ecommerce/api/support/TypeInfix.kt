package back.ecommerce.api.support

import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation


infix fun String.type(docsFieldType: DocsFieldType): Field {
    val field = createField(this, docsFieldType.type)
    when (docsFieldType) {
        is DATE -> field formattedAs "2024-01-01"
        is DATETIME -> field formattedAs "2024-01-01T00:00:00"
        else -> {}
    }
    return field
}

private fun createField(value: String, type: JsonFieldType, optional: Boolean = false): Field {
    val descriptor = PayloadDocumentation.fieldWithPath(value)
        .type(type)
        //.attributes(RestDocsUtils.emptyFormat(), RestDocsUtils.emptyDefaultValue())
        .description("")
    if (optional) descriptor.optional()

    return Field(descriptor)
}
