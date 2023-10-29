package back.ecommerce.product.dto.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductSortCondition {
	PRICE_LOW("price_low"), PRICE_HIGH("price_high"),
	NEW("new");

	private final String value;

	public static ProductSortCondition createWithSortQuery(String value) {
		try {
			return ProductSortCondition.valueOf(value.toUpperCase());
		} catch (NullPointerException | IllegalArgumentException exception) {
			return NEW;
		}
	}
}


