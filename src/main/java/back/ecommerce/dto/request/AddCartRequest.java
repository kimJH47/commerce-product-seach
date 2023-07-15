package back.ecommerce.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCartRequest {
	@NotBlank(message = "이메일은 필수적으로 필요합니다.")
	@Email(message = "옳바른 이메일 형식이 아닙니다.")
	private String email;
	@NotNull(message = "상품의 아이디는 필수적으로 필요합니다.")
	private Long productId;
	@Min(value = 1,message = "상품의 갯수는 최소 1개 이상이여야 합니다.")
	private Integer quantity;

}