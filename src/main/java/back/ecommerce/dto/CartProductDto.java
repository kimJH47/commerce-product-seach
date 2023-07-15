package back.ecommerce.dto;

import back.ecommerce.domain.product.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CartProductDto {
	private final Long id;
	private final String name;
	private final String brandName;
	private final Long price;
	private final Category category;
	private final int quantity;
}
