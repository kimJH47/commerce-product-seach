package back.ecommerce.dto.response.admin;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import back.ecommerce.domain.product.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddRequestProductResponse {
	private final String email;
	private final Long productId;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private final LocalDateTime requestTime;
	private ApprovalStatus approvalStatus;
}
