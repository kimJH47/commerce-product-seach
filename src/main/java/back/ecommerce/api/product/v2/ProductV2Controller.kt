package back.ecommerce.api.product.v2

import back.ecommerce.api.dto.Response
import back.ecommerce.api.resolver.ProductSearchRequestMapping
import back.ecommerce.common.logging.aop.annotation.Logging
import back.ecommerce.product.application.ProductV2Service
import back.ecommerce.product.dto.request.ProductSearchConditionRequest
import back.ecommerce.product.entity.Category
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2")
class ProductV2Controller(
    val productV2Service: ProductV2Service
) {

    @GetMapping("/categories/{category}")
    fun findProductsByCategory(@PathVariable("category") category: String): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "상품이 성공적으로 조회 되었습니다.",
            productV2Service.findWithCategoryAndPagination(Category.from(category))
        )
    }

    @Logging
    @GetMapping("/categories/{category}/detail")
    fun findProductWithPagination(@ProductSearchRequestMapping request: ProductSearchConditionRequest)
            : ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "상품이 성공적으로 조회 되었습니다.",
            productV2Service.findWithSearchCondition(request.category, request.parameters)
        )
    }

    @GetMapping("/product/{id}")
    fun findOne(@PathVariable("id") id: Long): ResponseEntity<Response> {
        return Response.createSuccessResponse("상품이 성공적으로 조회 되었습니다.", productV2Service.findOne(id))
    }
}