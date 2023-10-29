package back.ecommerce.product.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductListResponse {
	private int totalCount;
	private List<ProductDto> products;
}
