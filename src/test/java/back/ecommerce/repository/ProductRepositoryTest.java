package back.ecommerce.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import back.ecommerce.constant.PageConstant;
import back.ecommerce.domain.product.Category;
import back.ecommerce.domain.product.Product;
import back.ecommerce.dto.ProductDto;
import back.ecommerce.dto.ProductSearchCondition;
import back.ecommerce.dto.ProductSortCondition;

@DataJpaTest
@Import(QueryDSLRepoConfig.class)
class ProductRepositoryTest {

	@Autowired
	ProductQueryDslRepository productQueryDslRepository;
	@Autowired
	ProductRepository productRepository;

	@Autowired
	TestEntityManager testEntityManager;

	@Test
	@DisplayName("카테고리에 해당하는 상품이 가장 최근 등록된 순으로 default PageSize 인 20 개만큼 조회되어야한다.")
	void find_categories_pagination() {
		//given
		for (int i = 0; i < 50; i++) {
			productRepository.save(new Product(null, "name" + i, "brandA" + i, 10000L * i, Category.TOP));
		}
		for (int i = 0; i < 50; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, 10000L * i, Category.ONEPIECE));
		}

		//when
		List<ProductDto> products = productQueryDslRepository.findByCategoryWithPaginationOrderByBrandNew(
			Category.TOP, PageRequest.of(PageConstant.DEFAULT__PAGE, PageConstant.DEFAULT_PAGE_SIZE));

		//then
		assertThat(products).hasSize(20);
		assertThat(products).extracting(ProductDto::getId)
			.allMatch(id -> id <= 50 && id >= 20);
		assertThat(products).extracting(ProductDto::getCategory)
			.containsOnly(Category.TOP);
	}

	@Test
	@DisplayName("상세검색 이용 시 상세검색 조건에 해당하는 상품결과들이 페이징되어(20개) 조회되어야 한다.")
	void find_detail(){

		//given
		for (int i = 1; i <= 50; i++) {
			productRepository.save(new Product(null, "name" + i, "brandA" + i, 20000L + i, Category.TOP));
		}

		for (int i = 0; i < 50; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, 10000L + i, Category.TOP));
		}

		ProductSearchCondition productSearchCondition1 = new ProductSearchCondition(Category.TOP, "am", "an",
			500L,
			20000L, ProductSortCondition.PRICE_LOW,
			PageRequest.of(0, 20));

		ProductSearchCondition productSearchCondition2 = new ProductSearchCondition(Category.TOP, "therN", "B",
			1000L,
			20000L, ProductSortCondition.PRICE_HIGH,
			PageRequest.of(0, 20));

		//when
		List<ProductDto> products1 = productQueryDslRepository.findBySearchCondition(productSearchCondition1);
		List<ProductDto> products2 = productQueryDslRepository.findBySearchCondition(productSearchCondition2);

		//then
		assertThat(products1).hasSize(20)
			.extracting("price",Long.class)
			.isSorted();
		assertThat(products2).hasSize(20)
			.extracting("price", Long.class)
			.isSortedAccordingTo(Comparator.reverseOrder());

	}
}