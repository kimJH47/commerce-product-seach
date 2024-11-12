package back.ecommerce.api.support

import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Attributes


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

infix fun String.query(docsFieldType: DocsFieldType): QueryParam {
    val createQueryParam = createQueryParam(this, docsFieldType.type)
    return createQueryParam
}

private fun createQueryParam(value: String, type: JsonFieldType, optional: Boolean = false): QueryParam {
    val descriptor = RequestDocumentation.parameterWithName(value)
        .attributes(Attributes.key("type").value(type.name))

    if (optional) descriptor.optional()

    return QueryParam(descriptor)
}