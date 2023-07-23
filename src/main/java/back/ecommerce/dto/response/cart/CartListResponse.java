package back.ecommerce.dto.response.cart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartListResponse {
	private final String email;
	private final CartProducts CartProducts;
}
