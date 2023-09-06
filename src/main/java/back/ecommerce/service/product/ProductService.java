package back.ecommerce.service.product;

import static back.ecommerce.constant.PageConstant.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.response.product.ProductDto;
import back.ecommerce.dto.request.product.ProductSearchCondition;
import back.ecommerce.dto.response.product.ProductListResponse;
import back.ecommerce.repository.product.ProductQueryDslRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductQueryDslRepository productQueryDslRepository;

	public ProductListResponse findWithCategoryAndPagination(Category category) {
		List<ProductDto> products = productQueryDslRepository.findByCategoryWithPaginationOrderByBrandNew(category,
			PageRequest.of(DEFAULT__PAGE, DEFAULT_PAGE_SIZE));
		return new ProductListResponse(products.size(), products);
	}

	public ProductListResponse findWithSearchCondition(Category category, Map<String, String> parameters) {
		ProductSearchCondition searchCondition = ProductSearchCondition.createWithCategoryAndAttributes(
			category, parameters);
		List<ProductDto> products = productQueryDslRepository.findBySearchCondition(searchCondition);
		return new ProductListResponse(products.size(), products);
	}
}
