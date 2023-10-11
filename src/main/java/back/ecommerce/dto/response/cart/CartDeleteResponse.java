package back.ecommerce.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CartDeleteResponse {
	private final String email;
	private final Long id;
}
