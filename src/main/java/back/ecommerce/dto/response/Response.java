package back.ecommerce.dto.response;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Response {
	private String message;



	public static <T> ResponseEntity<Response> createSuccessResponse(String message, T entity) {
		return ResponseEntity.ok()
			.body(new SuccessResponse<>(message, entity));
	}
}
