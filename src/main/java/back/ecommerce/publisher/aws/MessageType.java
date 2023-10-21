package back.ecommerce.publisher.aws;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
	VERIFIED_CODE("001"),
	REQUEST_PRODUCT_APPROVAL_STATUS("002");

	private final String code;

}
