package back.ecommerce.exception;

public class AuthenticationException extends CustomException {
	public AuthenticationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
