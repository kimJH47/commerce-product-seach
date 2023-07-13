package back.ecommerce.exception;

public class TokenHasInvalidException extends RuntimeException {
	public TokenHasInvalidException(String message) {
		super(message);
	}
}
