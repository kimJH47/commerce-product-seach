package back.ecommerce.exception;

public class TokenHasExpiredException extends RuntimeException {
	public TokenHasExpiredException(String message) {
		super(message);
	}
}
