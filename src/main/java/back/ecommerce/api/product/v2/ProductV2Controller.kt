package back.ecommerce.api.product.v2

import back.ecommerce.api.dto.Response
import back.ecommerce.api.resolver.ProductSearchRequestMapping
import back.ecommerce.common.logging.aop.annotation.Logging
import back.ecommerce.product.dto.request.ProductSearchConditionRequest
import back.ecommerce.product.entity.Category
import back.ecommerce.product.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2")
class ProductV2Controller(
    val productService: ProductService
) {

    @GetMapping("/categories/{category}")
    fun getProductsByCategory(@PathVariable("category") category: String): ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "상품이 성공적으로 조회 되었습니다.",
            productService.findWithCategoryAndPagination(Category.from(category))
        )
    }

    @Logging
    @GetMapping("/categories/{category}/detail")
    fun findProductWithPagination(@ProductSearchRequestMapping request: ProductSearchConditionRequest)
            : ResponseEntity<Response> {
        return Response.createSuccessResponse(
            "상품이 성공적으로 조회 되었습니다.",
            productService.findWithSearchCondition(request.category, request.parameters)
        )
    }

    @GetMapping("/product/{id}")
    fun findOne(@PathVariable("id") id: Long): ResponseEntity<Response> {
        return Response.createSuccessResponse("상품이 성공적으로 조회 되었습니다.", productService.findOne(id))
    }
}