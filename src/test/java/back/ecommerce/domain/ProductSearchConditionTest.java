package back.ecommerce.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import back.ecommerce.constant.PageConstant;
import back.ecommerce.domain.condition.ProductSearchCondition;
import back.ecommerce.domain.condition.ProductSortCondition;
import back.ecommerce.domain.product.Category;

class ProductSearchConditionTest {

	@Test
	@DisplayName("카테고리와 파리미터 맵을 받아서 컨디션이 생성되어야 한다.")
	void create() {
		//given
		Category category = Category.TOP;
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("name", "nameA");
		hashMap.put("brandName", "brandName");
		hashMap.put("minPrice", "1");
		hashMap.put("maxPrice", "10000");
		hashMap.put("sort", "new");
		hashMap.put("page", "3");

		//when
		ProductSearchCondition actual = ProductSearchCondition.createWithCategoryAndAttributes(category,
			hashMap);

		//then
		assertThat(actual.getCategory()).isEqualTo(category);
		assertThat(actual.getName()).isEqualTo("nameA");
		assertThat(actual.getBranName()).isEqualTo("brandName");
		assertThat(actual.getMinPrice()).isEqualTo(1L);
		assertThat(actual.getMaxPrice()).isEqualTo(10000L);
		assertThat(actual.getSortCondition()).isEqualTo(ProductSortCondition.NEW);
		assertThat(actual.getPageSize()).isEqualTo(PageConstant.DEFAULT_PAGE_SIZE);
		assertThat(actual.getOffset()).isEqualTo(PageConstant.DEFAULT_PAGE_SIZE * 2);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"#", "-1", "0"})
	@DisplayName("유효하지않은 문자열이거나 값이 1 이상이 아니면 null 을 반환 해야한다.")
	void create_invalid_price(String price) {
		//given
		Category category = Category.TOP;
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("name", "nameA");
		hashMap.put("brandName", "brandName");
		hashMap.put("minPrice", price);
		hashMap.put("maxPrice", price);
		hashMap.put("sort", "new");
		hashMap.put("page", "3");

		//when
		ProductSearchCondition actual = ProductSearchCondition.createWithCategoryAndAttributes(category,
			hashMap);

		//then
		assertThat(actual.getMinPrice()).isEqualTo(null);
		assertThat(actual.getMaxPrice()).isEqualTo(null);

	}
}