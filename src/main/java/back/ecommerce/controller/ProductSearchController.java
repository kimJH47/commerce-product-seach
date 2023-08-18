package back.ecommerce.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.request.product.ProductSearchCondition;
import back.ecommerce.dto.response.common.Response;
import back.ecommerce.service.product.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductSearchController {

	private final ProductService productService;

	@GetMapping("/categories/{value}")
	public ResponseEntity<Response> findProductWithCategory(@PathVariable String value) {
		return Response.createSuccessResponse("상품이 성공적으로 조회 되었습니다."
			, productService.findWithCategoryAndPagination(Category.fromString(value)));
	}

	@GetMapping("/categories/{category}/detail")
	public ResponseEntity<Response> findProductWithPagination(@PathVariable String category,
		@RequestParam Map<String, String> params) {
		ProductSearchCondition productSearchCondition = ProductSearchCondition.fromQueryParameter(
			Category.fromString(category), params);
		return Response.createSuccessResponse("상품이 성공적으로 조회 되었습니다.",
			productService.findWithSearchCondition(productSearchCondition));
	}
}
