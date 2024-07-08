package back.ecommerce.api.spec

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.web.client.RestOperations

@WebMvcTest
@AutoConfigureRestDocs
abstract class ApiTestSpec(
    body: DescribeSpec.() -> Unit = {}
) : DescribeSpec(body)