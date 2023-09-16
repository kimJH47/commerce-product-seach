package back.ecommerce.exception;

public class InternalServerException extends CustomException {
	public InternalServerException(ErrorCode errorCode) {
		super(errorCode);
	}
}
