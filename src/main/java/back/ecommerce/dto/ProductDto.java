package back.ecommerce.dto;

import back.ecommerce.domain.product.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProductDto {
	private final Long id;
	private final String name;
	private final String brandName;
	private final Long price;
	private final Category category;
}
