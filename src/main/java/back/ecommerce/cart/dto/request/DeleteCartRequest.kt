package back.ecommerce.cart.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeleteCartRequest {
	private String email;
	private Long cartId;
}
