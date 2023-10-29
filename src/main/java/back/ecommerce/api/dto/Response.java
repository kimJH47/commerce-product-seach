package back.ecommerce.api.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import back.ecommerce.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Response {
	private String message;

	public static <T> ResponseEntity<Response> createSuccessResponse(String message, T entity) {
		return ResponseEntity.ok().body(new SuccessResponse<>(message, entity));
	}

	public static ResponseEntity<Response> createBadRequest(String message, MethodArgumentNotValidException e) {
		return ResponseEntity.badRequest().body(new FailedResponse(message, e));
	}

	public static ResponseEntity<Response> Failed(String message, CustomException exception) {
		return ResponseEntity.status(exception.getStatusCode())
			.body(new FailedResponse(message, exception.getReasonField(), exception.getDescription()));
	}
}
