package back.ecommerce.dto;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductSortCondition {
	PRICE_LOW("price_low"), PRICE_HIGH("price_high"),
	NEW("new"), NONE("");

	private final String value;

	public static ProductSortCondition createWithSortQuery(String queryString) {
		return Arrays.stream(ProductSortCondition.values())
			.filter(productSortCondition -> productSortCondition.getValue().equalsIgnoreCase(queryString))
			.findAny()
			.orElse(NEW);
	}
}


