package back.ecommerce.product.dto.response;

import back.ecommerce.product.entity.Category;
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
