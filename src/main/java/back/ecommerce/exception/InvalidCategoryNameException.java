package back.ecommerce.exception;

public class InvalidCategoryNameException extends RuntimeException {
	public InvalidCategoryNameException(String message) {
		super(message);
	}
}
