package back.ecommerce.dto.request.product;

import java.util.Map;

import back.ecommerce.domain.condition.PageCondition;
import back.ecommerce.domain.product.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProductSearchCondition {

	private static final String NUMBER_REGEX = "\\d+";

	private final Category category;
	private final String name;
	private final String branName;
	private final Long minPrice;
	private final Long MaxPrice;
	private final ProductSortCondition sortCondition;
	private final PageCondition pageCondition;

	public long getOffset() {
		return pageCondition.getOffset();
	}

	public long getPageSize() {
		return pageCondition.getPageSize();
	}

	public static ProductSearchCondition createWithCategoryAndAttributes(Category category,
		Map<String, String> attributes) {
		return new ProductSearchCondition(category,
			attributes.getOrDefault("name", null),
			attributes.getOrDefault("brandName", null),
			getMinPrice(attributes),
			getMaxPrice(attributes),
			getSortCondition(attributes),
			PageCondition.create(attributes.getOrDefault("page", ""))
		);
	}

	private static Long getMaxPrice(final Map<String, String> attributes) {
		String maxPrice = attributes.get("maxPrice");
		if (maxPrice == null || maxPrice.isEmpty() || !maxPrice.matches(NUMBER_REGEX)) {
			return null;
		}
		long price = Long.parseLong(maxPrice);
		return price > 0 ? price : null;
	}

	private static Long getMinPrice(final Map<String, String> attributes) {
		String minPrice = attributes.get("minPrice");
		if (minPrice == null || minPrice.isEmpty() || !minPrice.matches(NUMBER_REGEX)) {
			return null;
		}
		long price = Long.parseLong(minPrice);
		return price > 0 ? price : null;
	}

	private static ProductSortCondition getSortCondition(Map<String, String> attributes) {
		return ProductSortCondition.createWithSortQuery(attributes.get("sort"));
	}
}