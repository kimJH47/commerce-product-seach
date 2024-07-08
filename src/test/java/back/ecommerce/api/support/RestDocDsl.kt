package back.ecommerce.api.support

import jakarta.validation.Payload
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.RequestFieldsSnippet
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.restdocs.request.PathParametersSnippet
import org.springframework.restdocs.request.QueryParametersSnippet
import org.springframework.test.web.servlet.ResultActionsDsl

class RestDocDsl {

    private var pathParametersSnippet: PathParametersSnippet? = null

    private var requestFieldsSnippet: RequestFieldsSnippet? = null

    private var responseFieldsSnippet: ResponseFieldsSnippet? = null

    private var queryParametersSnippet: QueryParametersSnippet? = null

    fun requestFields(vararg fields: Field) {
        val requestFields = PayloadDocumentation.requestFields()
        requestFieldsSnippet = PayloadDocumentation.requestFields(fields.map { it.fieldDescriptor })

    }

    fun responseFields(vararg fields: Field) {
        responseFieldsSnippet = PayloadDocumentation.responseFields(fields.map { it.fieldDescriptor })
    }

    fun perform(identifier: String, resultActionDsl: ResultActionsDsl): ResultActionsDsl {
        return resultActionDsl.andDo {
            handle(
                MockMvcRestDocumentation.document(
                    identifier,
                    preprocessRequest,
                    preprocessResponse,
                    *listOfNotNull(
                        pathParametersSnippet,
                        requestFieldsSnippet,
                        responseFieldsSnippet,
                        queryParametersSnippet
                    ).toTypedArray()
                )
            )
        }
    }

    companion object {

        val preprocessRequest: OperationRequestPreprocessor = Preprocessors.preprocessRequest(
            Preprocessors.prettyPrint(),
        )

        val preprocessResponse: OperationResponsePreprocessor = Preprocessors.preprocessResponse(
            Preprocessors.prettyPrint(),
        )
    }

}
