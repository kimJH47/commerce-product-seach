package back.ecommerce.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final int statusCode;
	private final String reasonField;
	private final String description;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getDescription());
		statusCode = errorCode.getStatus();
		reasonField = errorCode.getReasonField();
		description = errorCode.getDescription();
	}
}
