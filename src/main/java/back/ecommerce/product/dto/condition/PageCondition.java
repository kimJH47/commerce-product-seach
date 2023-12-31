package back.ecommerce.product.dto.condition;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import back.ecommerce.common.constant.PageConstant;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PageCondition {
	private static final int GAP_START_PAGE_NUMBER = 1;
	private static final int INVALID_PAGE_NUMBER = 0;
	private static final String NUMBER_REGEX = "\\d+";

	private final Pageable pageable;

	public long getOffset() {
		return pageable.getOffset();
	}

	public long getPageSize() {
		return pageable.getPageSize();
	}

	public static PageCondition create(String pageNumber) {
		validatePageNumber(pageNumber);
		return new PageCondition(PageRequest.of(Integer.parseInt(pageNumber) - GAP_START_PAGE_NUMBER,
			PageConstant.DEFAULT_PAGE_SIZE));
	}

	private static void validatePageNumber(String pageNumber) {
		if (!StringUtils.hasText(pageNumber) || !pageNumber.matches(NUMBER_REGEX)
			|| Integer.parseInt(pageNumber) <= INVALID_PAGE_NUMBER) {
			throw new CustomException(ErrorCode.INVALID_PAGE_NUMBER);
		}
	}
}
