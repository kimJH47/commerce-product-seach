package back.ecommerce.domain.product;

import back.ecommerce.exception.InvalidCategoryNameException;

public enum Category {
	TOP, OUTER, PANTS, ONEPIECE, SKIRT, SNEAKERS, SHOES, HEAD_WEAR, ACCESSORY;

	public static Category from(String value) {
		try {
			return Category.valueOf(value.toUpperCase());
		} catch (NullPointerException |  IllegalArgumentException e) {
			throw new InvalidCategoryNameException("유효하지 않은 카테고리명 입니다.");
		}
	}
}

