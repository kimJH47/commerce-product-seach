package back.ecommerce.controller.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import back.ecommerce.dto.response.common.Response;
import back.ecommerce.exception.AuthHeaderInvalidException;
import back.ecommerce.exception.PasswordNotMatchedException;
import back.ecommerce.exception.ProductNotFoundException;
import back.ecommerce.exception.TokenHasExpiredException;
import back.ecommerce.exception.TokenHasInvalidException;
import back.ecommerce.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String BAD_REQUEST = "잘못된 요청입니다.";

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response> handle(MethodArgumentNotValidException e) {
		return Response.createBadRequest(BAD_REQUEST, e);
	}

	@ExceptionHandler(value = {UserNotFoundException.class, PasswordNotMatchedException.class})
	public ResponseEntity<Response> handle(Exception e) {
		return Response.createBadRequest(BAD_REQUEST, "login", e.getMessage());
	}

	@ExceptionHandler(AuthHeaderInvalidException.class)
	public ResponseEntity<Response> handle(AuthHeaderInvalidException e) {
		return Response.createBadRequest(BAD_REQUEST, "authorizationHeader", e.getMessage());
	}

	@ExceptionHandler({TokenHasInvalidException.class, TokenHasExpiredException.class})
	public ResponseEntity<Response> handleTokenException(Exception e) {
		return Response.createBadRequest(BAD_REQUEST, "accessToken", e.getMessage());
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<Response> handle(ProductNotFoundException e) {
		return Response.createBadRequest(BAD_REQUEST, "product", e.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Response> handle(IllegalArgumentException e) {
		return Response.createBadRequest(BAD_REQUEST, "argument", e.getMessage());
	}
}
