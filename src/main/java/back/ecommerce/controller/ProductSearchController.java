package back.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.response.Response;
import back.ecommerce.service.ProductService;
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
}
