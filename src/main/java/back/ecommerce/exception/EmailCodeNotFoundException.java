package back.ecommerce.exception;

public class EmailCodeNotFoundException extends RuntimeException {
	public EmailCodeNotFoundException(String message) {
		super(message);
	}
}
