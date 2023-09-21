package back.ecommerce.dto.request.amdin;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import back.ecommerce.domain.product.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest {
	@NotNull(message = "등록요청 상품 아아디는 필수적으로 필요합니다.")
	private Long requestId;
	@NotNull(message = "승인상태는 필수적로 필요합니다.")
	private ApprovalStatus approvalStatus;
	@NotBlank(message = "이메일은 필수적으로 필요합니다.")
	@Email(message = "옳바른 이메일 형식이 아닙니다.")
	private String email;
}
