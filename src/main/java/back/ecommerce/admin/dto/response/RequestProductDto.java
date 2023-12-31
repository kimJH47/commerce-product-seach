package back.ecommerce.admin.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import back.ecommerce.product.entity.ApprovalStatus;
import back.ecommerce.product.entity.Category;
import back.ecommerce.product.entity.RequestProduct;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder()
@Getter
public class RequestProductDto {
	private final Long requestId;
	private final String email;
	private final String name;
	private final String brandName;
	private final Category category;
	private final Long price;
	private final ApprovalStatus approvalStatus;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private final LocalDateTime requestTime;

	public static RequestProductDto create(RequestProduct requestProduct) {
		return RequestProductDto.builder()
			.requestId(requestProduct.getId())
			.email(requestProduct.getEmail())
			.name(requestProduct.getName())
			.brandName(requestProduct.getBrandName())
			.category(requestProduct.getCategory())
			.price(requestProduct.getPrice())
			.approvalStatus(requestProduct.getApprovalStatus())
			.requestTime(requestProduct.getCreatedDate())
			.build();
	}
}
