package back.ecommerce.dto.response;

import java.util.List;

import back.ecommerce.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductListResponse {
	private int totalCount;
	private List<ProductDto> products;
}
