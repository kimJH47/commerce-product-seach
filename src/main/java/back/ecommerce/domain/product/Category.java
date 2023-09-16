package back.ecommerce.domain.product;

import static back.ecommerce.exception.ErrorCode.*;

import back.ecommerce.exception.CustomException;

public enum Category {
	TOP, OUTER, PANTS, ONEPIECE, SKIRT, SNEAKERS, SHOES, HEAD_WEAR, ACCESSORY;

	public static Category from(String value) {
		try {
			return Category.valueOf(value.toUpperCase());
		} catch (NullPointerException | IllegalArgumentException e) {
			throw new CustomException(INVALID_CATEGORY);
		}
	}
}

