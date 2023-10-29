package back.ecommerce.admin.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import back.ecommerce.product.entity.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddRequestProductResponse {
	private final String email;
	private final Long requestId;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private final LocalDateTime requestTime;
	private ApprovalStatus approvalStatus;
}
