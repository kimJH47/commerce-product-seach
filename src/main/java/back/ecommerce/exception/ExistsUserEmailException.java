package back.ecommerce.exception;

public class ExistsUserEmailException extends RuntimeException {
	public ExistsUserEmailException(String message) {
		super(message);
	}
}
