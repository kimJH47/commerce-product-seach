package back.ecommerce.dto.response.product;

import back.ecommerce.domain.product.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductDto {
	private Long id;
	private String name;
	private String brandName;
	private Long price;
	private Category category;
}
