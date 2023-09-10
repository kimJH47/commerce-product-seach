package back.ecommerce.domain.condition;

import static back.ecommerce.constant.PageConstant.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import back.ecommerce.exception.InvalidPageNumberException;

class PageConditionTest {

	@Test
	@DisplayName("페이지 번호와 오프셋을 가지고있는 컨디션이 생성되어야한다.")
	void create() {
		//given
		String pageNumber = "10";

		//when
		PageCondition actual = PageCondition.create(pageNumber);

		//then
		assertThat(actual.getPageSize()).isEqualTo(DEFAULT_PAGE_SIZE);
		assertThat(actual.getOffset()).isEqualTo(DEFAULT_PAGE_SIZE * 9);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"1#", "-1", "c", "0"})
	@DisplayName("페이지 번호의 값이 null, empty, 1 이상의 정수가 아니면 InvalidPageNumberException 가 발생한다.")
	void create_exception(String pageNumber) {
		//expect
		assertThatThrownBy(() -> PageCondition.create(pageNumber))
			.isInstanceOf(InvalidPageNumberException.class)
			.hasMessage("유효하지 않는 페이지 번호입니다.");
	}

}