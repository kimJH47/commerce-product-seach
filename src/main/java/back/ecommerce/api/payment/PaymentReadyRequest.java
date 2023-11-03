package back.ecommerce.api.payment;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentReadyRequest {
	@NotBlank(message = "이메일은 필수적으로 필요합니다.")
	@Email(message = "옳바른 이메일 형식이 아닙니다.")
	private String email;
	@Min(value = 1, message = "상품의 전체갯수는 최소 1개 이상이여야 합니다.")
	private Long totalCount;
	@Min(value = 1, message = "상품의 전체금액은 최소 1 이상이여야 합니다.")
	private Long totalPrice;
	private List<OrderProductDto> orderProducts;
}
