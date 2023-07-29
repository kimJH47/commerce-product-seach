package back.ecommerce.domain.product;

import java.util.Arrays;

public enum Category {
	TOP, OUTER, PANTS, ONEPIECE, SKIRT, SNEAKERS, SHOES, HEAD_WEAR, ACCESSORY;

	public static Category fromString(String value) {
		return Arrays.stream(Category.values())
			.limit(1)
			.filter(category -> category.toString().equalsIgnoreCase(value))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("일치하는 카테고리가 없습니다."));
	}

}
