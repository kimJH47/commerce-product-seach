package back.ecommerce.dto.request.product;

import static back.ecommerce.constant.PageConstant.*;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import back.ecommerce.domain.product.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class ProductSearchCondition {

	private final Category category;
	private final String name;
	private final String branName;
	private final Long minPrice;
	private final Long MaxPrice;
	private final ProductSortCondition sortCondition;
	private final Pageable pageable;

	public static ProductSearchCondition fromQueryParameter(Category category, Map<String, String> params) {
		return new ProductSearchCondition(
			category,
			params.get("name"),
			params.get("brandName"),
			Long.parseLong(params.get("minPrice")),
			Long.parseLong(params.get("maxPrice")),
			ProductSortCondition.createWithSortQuery((params.get("sort"))),
			PageRequest.of(Integer.parseInt(params.get("page")) - 1, DEFAULT_PAGE_SIZE)
		);
	}

	public long getOffset() {
		return pageable.getOffset();
	}

	public long getPageSize() {
		return pageable.getPageSize();
	}
}