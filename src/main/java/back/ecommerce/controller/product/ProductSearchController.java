package back.ecommerce.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.common.logging.aop.annotation.Logging;
import back.ecommerce.controller.resolver.ProductSearchRequestMapping;
import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.request.product.ProductSearchConditionRequest;
import back.ecommerce.dto.response.common.Response;
import back.ecommerce.service.product.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductSearchController {

	private final ProductService productService;

	@GetMapping("/categories/{category}")
	public ResponseEntity<Response> findProductWithCategory(@PathVariable String category) {
		return Response.createSuccessResponse("상품이 성공적으로 조회 되었습니다."
			, productService.findWithCategoryAndPagination(Category.from(category)));
	}

	@Logging
	@GetMapping("/categories/**/detail")
	public ResponseEntity<Response> findProductWithPagination(
		@ProductSearchRequestMapping ProductSearchConditionRequest request) {
		return Response.createSuccessResponse("상품이 성공적으로 조회 되었습니다.",
			productService.findWithSearchCondition(request.getCategory(), request.getParameters()));
	}
}
