package back.ecommerce.dto.response.admin;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import back.ecommerce.domain.product.ApprovalStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestProductDto {
	private final Long requestId;
	private final String email;
	private final String name;
	private final String brandName;
	private final Long price;
	private final ApprovalStatus approvalStatus;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private final LocalDateTime requestTime;
}
