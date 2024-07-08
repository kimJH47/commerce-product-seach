package back.ecommerce.api.support

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.snippet.Attributes


open class Field(
    val fieldDescriptor: FieldDescriptor
) {
    val isIgnored: Boolean = fieldDescriptor.isIgnored
    val isOptional: Boolean = fieldDescriptor.isOptional

    open infix fun means(value: String): Field {
        fieldDescriptor.description(value)
        return this
    }

    open infix fun attributes(block: Field.() -> Unit): Field {
        block()
        return this
    }

    open infix fun formattedAs(value: String): Field {
        fieldDescriptor.attributes(Attributes.key("format").value(value))
        return this
    }

    open infix fun isOptional(value: Boolean): Field {
        if (value) fieldDescriptor.optional()
        return this
    }

    open infix fun isIgnored(value: Boolean): Field {
        if (value) fieldDescriptor.ignored()
        return this
    }
}
