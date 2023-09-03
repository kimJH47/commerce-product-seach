package back.ecommerce.domain.product;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import back.ecommerce.exception.InvalidCategoryNameException;

class CategoryTest {

	@Test
	@DisplayName("대소문자 상관없이 문자열을 받아서 해당하는 카테고리가 생성되어야 한다.")
	void create() {
		//given
		String value1 = "TOP";
		String value2 = "pants";

		//when
		Category top = Category.from(value1);
		Category pants = Category.from(value2);

		//then
		assertThat(top).isEqualTo(Category.TOP);
		assertThat(pants).isEqualTo(Category.PANTS);

	}

	@Test
	@DisplayName("유효하지 않은 카테고리 문자열을 받으면 InvalidCategoryNameException 이 발생한다.")
	void test_invalidName() {
		//expect
		assertThatThrownBy(() -> Category.from("to1p"))
			.isInstanceOf(InvalidCategoryNameException.class)
			.hasMessage("유효하지 않은 카테고리명 입니다.");
	}

	@Test
	@DisplayName("파라미터의 인자가 null 이면 InvalidCategoryNameException 이 발생한다.")
	void test_nullValue() {
		//expect
		assertThatThrownBy(() -> Category.from(null))
			.isInstanceOf(InvalidCategoryNameException.class)
			.hasMessage("유효하지 않은 카테고리명 입니다.");

	}
}