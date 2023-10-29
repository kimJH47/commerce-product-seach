package back.ecommerce.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CartDeleteResponse {
	private final String email;
	private final Long id;
}
