package back.ecommerce.repository;

import static back.ecommerce.product.entity.Category.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import back.ecommerce.common.constant.PageConstant;
import back.ecommerce.config.jpa.JpaAuditingConfig;
import back.ecommerce.product.dto.condition.ProductSearchCondition;
import back.ecommerce.product.dto.response.ProductDto;
import back.ecommerce.product.dto.response.v2.ProductV2Dto;
import back.ecommerce.product.entity.Category;
import back.ecommerce.product.entity.Product;
import back.ecommerce.product.repository.ProductQueryDslRepository;
import back.ecommerce.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;

@DataJpaTest
@Import(value = {QueryDSLRepoConfig.class, JpaAuditingConfig.class})
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
			productRepository.save(new Product(null, "name" + i, "brandA" + i, 10000L * i, TOP));
		}
		for (int i = 0; i < 50; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, 10000L * i, ONEPIECE));
		}

		//when
		List<ProductDto> products = productQueryDslRepository.findByCategoryWithPaginationOrderByBrandNew(
			TOP, PageRequest.of(PageConstant.DEFAULT__PAGE, PageConstant.DEFAULT_PAGE_SIZE));

		//then
		assertThat(products).hasSize(12);
		assertThat(products).extracting(ProductDto::getCategory)
			.containsOnly(TOP);
	}

	@Test
	@DisplayName("상세 검색 시 정렬조건이 높은 가격 순 이면 해당하는 상품들이  페이징되어 조회되어야 한다.")
	void find_detail_price_high_sorted() {

		//given
		for (int i = 0; i < 5; i++) {
			productRepository.save(new Product(null, "name" + i, "brandA" + i, 20000L, TOP));
		}

		for (int i = 0; i < 15; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, 100L, TOP));
		}

		for (int i = 0; i < 10; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, 50L + i, TOP));
		}

		ProductSearchCondition condition = createCondition(TOP, "", "", null, null, "price_high", "1");

		//when
		List<ProductDto> products = productQueryDslRepository.findBySearchCondition(condition);

		//then
		assertThat(products).hasSize(12)
			.extracting("price", Long.class)
			.isSortedAccordingTo(Comparator.reverseOrder());

		long sumPrice = products.stream()
			.mapToLong(ProductDto::getPrice)
			.sum();
		assertThat(sumPrice).isEqualTo(100700L);
	}

	@Test
	@DisplayName("상세 검색 시 정렬조건이 낮은 가격 순 이면 해당하는 상품들이  페이징되어 조회되어야 한다.")
	void find_detail_price_low_sorted() {
		//given
		for (int i = 1; i <= 5; i++) {
			productRepository.save(new Product(null, "name" + i, "brandA" + i, 5000L, SKIRT));
		}

		for (int i = 0; i < 10; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, 10000L, SKIRT));
		}

		for (int i = 1; i <= 10; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, i * 15000L, SKIRT));
		}

		for (int i = 1; i <= 5; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, i * 1000L, SKIRT));
		}

		ProductSearchCondition condition = createCondition(SKIRT, "", "", null, null, "price_low", "1");

		//when
		List<ProductDto> products = productQueryDslRepository.findBySearchCondition(condition);

		//then
		assertThat(products)
			.filteredOn(productDto -> productDto.getCategory() == SKIRT)
			.extracting("price", Long.class)
			.hasSize(12)
			.isSorted();

		long sumPrice = products.stream()
			.mapToLong(ProductDto::getPrice)
			.sum();
		assertThat(sumPrice).isEqualTo(60000L);
	}

	@Test
	@DisplayName("상세검색 시 가격범위에 해당하는 상품들이 페이징되어 조회되어야 한다.")
	void find_detail_price_range_filter() {
		//given
		for (int i = 1; i <= 10; i++) {
			productRepository.save(new Product(null, "name" + i, "brandA" + i, 1000L, SKIRT));
		}

		for (int i = 0; i < 5; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, 10000L, SKIRT));
		}

		for (int i = 1; i <= 5; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, 15000L * i, SKIRT));
		}

		for (int i = 1; i <= 10; i++) {
			productRepository.save(new Product(null, "OtherName" + i, "brandB" + i, 100000L * i, SKIRT));
		}
		ProductSearchCondition condition = createCondition(SKIRT, "", "", "1000", "15000", "", "1");

		//when
		List<ProductDto> products = productQueryDslRepository.findBySearchCondition(condition);

		//then
		assertThat(products)
			.filteredOn(productDto -> productDto.getCategory().equals(SKIRT))
			.hasSize(12)
			.extracting("id", Long.class)
			.isSortedAccordingTo(Comparator.reverseOrder());

		long sumPrice = products.stream()
			.mapToLong(ProductDto::getPrice)
			.sum();

		assertThat(sumPrice).isEqualTo(71000L);
	}

	@Test
	@DisplayName("상세조건 검색 시 카테고리와 page 값만 존재하면 등록일순으로 카테고리만 조건을 가지고 조회 되어야한다.")
	void find_detail_null_sorted() {
		//given
		EntityManager entityManager = testEntityManager.getEntityManager();
		for (int i = 0; i < 23; i++) {
			Product entity = new Product(null, "headType" + i, "brandB" + i, 10000L + (i * 100), HEAD_WEAR);
			entityManager.persist(entity);
		}
		entityManager.clear();
		ProductSearchCondition condition = createCondition(HEAD_WEAR, null, null, null, null, null,
			"2");

		//when
		List<ProductDto> products = productQueryDslRepository.findBySearchCondition(condition);

		//then
		assertThat(products).hasSize(11)
			.filteredOn(productDto -> productDto.getCategory() == HEAD_WEAR)
			.extracting("id", Long.class)
			.isSortedAccordingTo(Comparator.reverseOrder());
	}

	@Test
	@DisplayName("상세조건 검색 시 이름을 포함하는 상품들이 페이징되어서 반환 되어야한다.")
	void find_detail_name_like() {
		//given
		EntityManager entityManager = testEntityManager.getEntityManager();
		for (int i = 0; i < 5; i++) {
			Product entity = new Product(null, "outerA" + i, "brandB" + i, 10000L + (i * 100), OUTER);
			entityManager.persist(entity);
		}

		for (int i = 0; i < 10; i++) {
			Product entity = new Product(null, "some" + i, "brandB" + i, 10000L + (i * 200), OUTER);
			entityManager.persist(entity);
		}

		for (int i = 0; i < 5; i++) {
			Product entity = new Product(null, "outerB" + i, "brandB" + i, 10000L + (i * 300), OUTER);
			entityManager.persist(entity);
		}

		entityManager.clear();
		ProductSearchCondition condition = createCondition(OUTER, "outer", "", null, null, null,
			"1");
		//when
		List<ProductDto> products = productQueryDslRepository.findBySearchCondition(condition);

		//then
		assertThat(products)
			.filteredOn(productDto -> productDto.getCategory() == OUTER)
			.hasSize(10);

	}

	@Test
	@DisplayName("상세조건 검색 시 브랜드 이름을 포함하는 상품들이 페이징되어서 반환 되어야한다.")
	void find_detail_brandName_like() {
		//given
		EntityManager entityManager = testEntityManager.getEntityManager();
		for (int i = 0; i < 5; i++) {
			Product entity = new Product(null, "outerA" + i, "brandA", 10000L + (i * 100), SHOES);
			entityManager.persist(entity);
		}

		for (int i = 0; i < 10; i++) {
			Product entity = new Product(null, "some" + i, "brandC", 10000L + (i * 200), SHOES);
			entityManager.persist(entity);
		}

		for (int i = 0; i < 15; i++) {
			Product entity = new Product(null, String.valueOf(i), "brandA", 10000L + (i * 300), SHOES);
			entityManager.persist(entity);
		}

		entityManager.clear();
		ProductSearchCondition condition = createCondition(SHOES, "", "brandA", null, null, null,
			"1");

		//when
		List<ProductDto> products = productQueryDslRepository.findBySearchCondition(condition);

		//then
		assertThat(products)
			.filteredOn(productDto -> productDto.getCategory() == SHOES)
			.hasSize(12);
	}

	@Test
	void findByIds() {
		//given
		ArrayList<Long> ids = new ArrayList<>();
		ids.add(productRepository.save(new Product(null, "상품1,", "브랜드", 100L, SHOES)).getId());
		ids.add(productRepository.save(new Product(null, "상품2,", "브랜드", 100L, SHOES)).getId());
		ids.add(productRepository.save(new Product(null, "상품3,", "브랜드", 100L, SHOES)).getId());
		productRepository.save(new Product(null, "다른상품,", "브랜드", 500L, SHOES));

		//when
		List<Product> actual = productRepository.findByIds(ids);

		//then
		assertThat(actual).hasSize(3);
		assertThat(actual)
			.filteredOn(product -> product.getName().startsWith("상품"))
			.hasSize(3);
	}

	@Test
	void findByCategoryWithPaginationOrderByBrandNewV2() {
		List<ProductV2Dto> byCategoryWithPaginationOrderByBrandNewV2 = productQueryDslRepository.findByCategoryWithPaginationOrderByBrandNewV2(
			ACCESSORY,
			PageRequest.of(PageConstant.DEFAULT__PAGE, PageConstant.DEFAULT_PAGE_SIZE));

	}

	private ProductSearchCondition createCondition(
		Category category,
		String name,
		String brandName,
		String minPrice,
		String maxPrice,
		String sort,
		String pageNumber
	) {
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("name", name);
		hashMap.put("brandName", brandName);
		hashMap.put("minPrice", minPrice);
		hashMap.put("maxPrice", maxPrice);
		hashMap.put("sort", sort);
		hashMap.put("page", pageNumber);
		return ProductSearchCondition.createWithCategoryAndAttributes(category, hashMap);
	}
}