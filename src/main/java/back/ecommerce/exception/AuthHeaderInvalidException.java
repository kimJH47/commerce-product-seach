package back.ecommerce.exception;

public class AuthHeaderInvalidException extends RuntimeException {
	public AuthHeaderInvalidException(String message) {
		super(message);
	}
}
