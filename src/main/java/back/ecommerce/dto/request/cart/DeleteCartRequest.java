package back.ecommerce.dto.request.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeleteCartRequest {
	private String email;
	private Long cartId;
}
