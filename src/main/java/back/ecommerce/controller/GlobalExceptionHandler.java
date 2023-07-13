package back.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import back.ecommerce.dto.response.Response;
import back.ecommerce.exception.PasswordNotMatchedException;
import back.ecommerce.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String BAD_REQUEST = "잘못된 요청입니다.";

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response> handle(MethodArgumentNotValidException e) {
		return Response.createBadRequest(BAD_REQUEST, e);
	}

	@ExceptionHandler(value = {UserNotFoundException.class, PasswordNotMatchedException.class})
	public ResponseEntity<Response> handle(RuntimeException e) {
		return Response.createBadRequest(BAD_REQUEST, "login", e.getMessage());
	}
}
