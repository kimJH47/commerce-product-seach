package back.ecommerce.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import back.ecommerce.domain.condition.ProductSearchCondition;
import back.ecommerce.domain.product.Category;
import back.ecommerce.dto.response.product.ProductDto;
import back.ecommerce.dto.response.product.ProductListResponse;
import back.ecommerce.exception.CustomException;
import back.ecommerce.repository.product.ProductQueryDslRepository;
import back.ecommerce.service.product.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	ProductQueryDslRepository productQueryDslRepository;
	@InjectMocks
	ProductService productService;

	@Test
	@DisplayName("카테고리를 받아서 카테고리에 해당하는 상품 20개(기본 페이지 사이즈)가 최근 순으로 정렬되어서 응답되어야 한다.")
	void findWithCategoryAndPagination() {
		//given
		ArrayList<ProductDto> products = new ArrayList<>();
		products.add(new ProductDto(1L, "productA", "brandA", 150000L, Category.HEAD_WEAR));
		products.add(new ProductDto(15L, "productB", "brandA", 153000L, Category.HEAD_WEAR));
		products.add(new ProductDto(17L, "productC", "brandB", 156000L, Category.HEAD_WEAR));
		products.add(new ProductDto(19L, "productD", "brandC", 80000L, Category.HEAD_WEAR));

		given(productQueryDslRepository.findByCategoryWithPaginationOrderByBrandNew(any(Category.class),
			any(Pageable.class)))
			.willReturn(products);

		//when
		ProductListResponse response = productService.findWithCategoryAndPagination(
			Category.HEAD_WEAR);

		//then
		assertThat(response.getTotalCount()).isEqualTo(4);
		assertThat(response.getProducts()).hasSize(4)
			.extracting("id", "name", "brandName", "price", "category")
			.containsExactlyInAnyOrder(
				tuple(1L, "productA", "brandA", 150000L, Category.HEAD_WEAR),
				tuple(15L, "productB", "brandA", 153000L, Category.HEAD_WEAR),
				tuple(17L, "productC", "brandB", 156000L, Category.HEAD_WEAR),
				tuple(19L, "productD", "brandC", 80000L, Category.HEAD_WEAR)
			);

		then(productQueryDslRepository).should(times(1))
			.findByCategoryWithPaginationOrderByBrandNew(any(Category.class), any(Pageable.class));
	}

	@Test
	@DisplayName("SearchCondition 을 받아서 해당하는 조건의 상품20개를 정렬하여 응답되어야한다.")
	void findWithSearchCondition() {
		//given
		ArrayList<ProductDto> products = new ArrayList<>();
		products.add(new ProductDto(1L, "productA", "brandA", 150000L, Category.HEAD_WEAR));
		products.add(new ProductDto(15L, "productB", "brandA", 153000L, Category.HEAD_WEAR));
		products.add(new ProductDto(17L, "productC", "brandB", 156000L, Category.HEAD_WEAR));
		products.add(new ProductDto(19L, "productD", "brandC", 80000L, Category.HEAD_WEAR));

		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("name", "product");
		parameters.put("brandName", "brand");
		parameters.put("page", "1");
		parameters.put("sort", "NEW");

		given(productQueryDslRepository.findBySearchCondition(any(ProductSearchCondition.class)))
			.willReturn(products);

		//when
		ProductListResponse actual = productService.findWithSearchCondition(Category.HEAD_WEAR, parameters);

		//then
		assertThat(actual.getProducts())
			.hasSize(4)
			.extracting(ProductDto::getId, ProductDto::getName)
			.containsExactlyInAnyOrder(
				tuple(1L, "productA"),
				tuple(15L, "productB"),
				tuple(17L, "productC"),
				tuple(19L, "productD")
			);
		then(productQueryDslRepository).should(times(1))
			.findBySearchCondition(any(ProductSearchCondition.class));
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "0", "-1", "gh"})
	@NullAndEmptySource
	@DisplayName("파라미터 중 page 값이 유효하지 않으면 InvalidPageNumberException 발생한다.")
	void findWithSearchCondition_invalidPage(String page) {
		//given
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("name", "product");
		parameters.put("brandName", "brand");
		parameters.put("page", page);
		parameters.put("sort", "NEW");

		//expect
		assertThatThrownBy(() -> productService.findWithSearchCondition(Category.ACCESSORY, parameters))
			.isInstanceOf(CustomException.class)
			.hasMessage("유효하지 않는 페이지 번호입니다.");

	}
}