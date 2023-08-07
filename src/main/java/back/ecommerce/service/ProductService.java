package back.ecommerce.service;

import static back.ecommerce.constant.PageConstant.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.ProductDto;
import back.ecommerce.dto.ProductSearchCondition;
import back.ecommerce.dto.response.ProductListResponse;
import back.ecommerce.repository.ProductQueryDslRepository;
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

	public ProductListResponse findWithSearchCondition(ProductSearchCondition productSearchCondition) {
		List<ProductDto> products = productQueryDslRepository.findBySearchCondition(productSearchCondition);
		return new ProductListResponse(products.size(), products);
	}
}
