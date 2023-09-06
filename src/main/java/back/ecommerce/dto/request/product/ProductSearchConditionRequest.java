package back.ecommerce.dto.request.product;

import java.util.Map;

import back.ecommerce.domain.product.Category;
import lombok.Getter;

@Getter
public class ProductSearchConditionRequest {
	private final Category category;
	private final Map<String, String> parameters;

	private ProductSearchConditionRequest(Category category, Map<String, String> parameters) {
		this.category = category;
		this.parameters = parameters;
	}

	public static ProductSearchConditionRequest create(Category category, Map<String, String> parameters) {
		validateEmptyCondition(parameters);
		return new ProductSearchConditionRequest(category, parameters);
	}

	private static void validateEmptyCondition(Map<String, String> parameters) {
		parameters.putIfAbsent("name", null);
		parameters.putIfAbsent("brandName", null);
		parameters.putIfAbsent("minPrice", null);
		parameters.putIfAbsent("maxPrice", null);
		parameters.putIfAbsent("sort", "new");
		parameters.putIfAbsent("page", "");
	}
}
