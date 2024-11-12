package back.ecommerce.api.support

import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.snippet.Attributes

open class QueryParam(
    val parameterDescriptor: ParameterDescriptor
) {

    open infix fun means(value: String): QueryParam {
        parameterDescriptor.description(value)
        return this
    }

    open infix fun attributes(block: QueryParam.() -> Unit): QueryParam {
        block()
        return this
    }

    open infix fun formattedAs(value: String): QueryParam {
        parameterDescriptor.attributes(Attributes.key("format").value(value))
        return this
    }

    open infix fun isOptional(value: Boolean): QueryParam {
        if (value) parameterDescriptor.optional()
        return this
    }

    open infix fun isIgnored(value: Boolean): QueryParam {
        if (value) parameterDescriptor.ignored()
        return this
    }

    open infix fun isRequired(value: Boolean): QueryParam {
        parameterDescriptor.attributes(Attributes.key("required").value(value))
        return this
    }
}