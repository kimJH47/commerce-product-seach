package back.ecommerce.api.payment;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PaymentReadyRequest {
	private String email;
	private Long totalCount;
	private Long totalPrice;
	private List<OrderProductDto> orderProducts;
}
