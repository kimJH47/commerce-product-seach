package back.ecommerce.service.product;

import static back.ecommerce.constant.PageConstant.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import back.ecommerce.domain.product.Category;
import back.ecommerce.domain.product.Product;
import back.ecommerce.dto.response.product.ProductDto;
import back.ecommerce.domain.condition.ProductSearchCondition;
import back.ecommerce.dto.response.product.ProductListResponse;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.repository.product.ProductQueryDslRepository;
import back.ecommerce.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductQueryDslRepository productQueryDslRepository;
	private final ProductRepository productRepository;

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

	public ProductDto findOne(Long id) {
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
		return new ProductDto(product.getId(), product.getName(), product.getBrandName(), product.getPrice(),
			product.getCategory());
	}
}
