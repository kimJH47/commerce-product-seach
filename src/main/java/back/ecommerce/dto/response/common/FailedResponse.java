package back.ecommerce.dto.response.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FailedResponse extends Response {
	private final Map<String, String> reasons = new HashMap<>();

	public FailedResponse(String message, MethodArgumentNotValidException e) {
		super(message);
		List<FieldError> fieldErrors = e.getFieldErrors();
		for (FieldError fieldError : fieldErrors) {
			reasons.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
	}

	public FailedResponse(String message, String field, String reason) {
		super(message);
		reasons.put(field, reason);
	}
}
