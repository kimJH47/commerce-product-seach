package back.ecommerce.repository;

import static org.assertj.core.api.Assertions.*;

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
}