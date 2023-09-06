package back.ecommerce.controller.common;

import static org.slf4j.event.Level.*;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.event.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import back.ecommerce.dto.response.common.FailedResponse;
import back.ecommerce.dto.response.common.Response;
import back.ecommerce.exception.AuthHeaderInvalidException;
import back.ecommerce.exception.InvalidCategoryNameException;
import back.ecommerce.exception.PasswordNotMatchedException;
import back.ecommerce.exception.ProductNotFoundException;
import back.ecommerce.exception.TokenHasExpiredException;
import back.ecommerce.exception.TokenHasInvalidException;
import back.ecommerce.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

	private static final String BAD_REQUEST = "잘못된 요청입니다.";
	private static final String INTERNAL_SERVER_ERROR = "서버에서 에러가 발생 했습니다.";

	private static final String ERROR_FORMAT =
		"[ERROR] {} {} -> {} : {}";

	private final GlobalLogger globalLogger;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response> handle(MethodArgumentNotValidException e, HttpServletRequest request) {
		logging(WARN, request, "request", e);
		return Response.createBadRequest(BAD_REQUEST, e);
	}

	@ExceptionHandler(value = {UserNotFoundException.class, PasswordNotMatchedException.class})
	public ResponseEntity<Response> handle(Exception e, HttpServletRequest request) {
		logging(INFO, request, "login", e);
		return Response.createBadRequest(BAD_REQUEST, "login", e.getMessage());
	}

	@ExceptionHandler(AuthHeaderInvalidException.class)
	public ResponseEntity<Response> handle(AuthHeaderInvalidException e, HttpServletRequest request) {
		logging(WARN, request, "authorizationHeader", e);
		return Response.createBadRequest(BAD_REQUEST, "authorizationHeader", e.getMessage());
	}

	@ExceptionHandler({TokenHasInvalidException.class, TokenHasExpiredException.class})
	public ResponseEntity<Response> handleTokenException(Exception e, HttpServletRequest request) {
		logging(WARN, request, "accessToken", e);
		return Response.createBadRequest(BAD_REQUEST, "accessToken", e.getMessage());
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<Response> handle(ProductNotFoundException e, HttpServletRequest request) {
		logging(INFO, request, "product", e);
		return Response.createBadRequest(BAD_REQUEST, "product", e.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Response> handle(IllegalArgumentException e, HttpServletRequest request) {
		logging(WARN, request, "argument", e);
		return Response.createBadRequest(BAD_REQUEST, "argument", e.getMessage());
	}

	@ExceptionHandler(InvalidCategoryNameException.class)
	public ResponseEntity<Response> handle(InvalidCategoryNameException e, HttpServletRequest request) {
		logging(INFO, request, "category", e);
		return Response.createBadRequest(BAD_REQUEST, "category", e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleInternalServerError(Exception e, HttpServletRequest request) {
		logging(ERROR, request, "server", e);
		return ResponseEntity.internalServerError()
			.body(new FailedResponse(INTERNAL_SERVER_ERROR, "server", e.getMessage()));
	}

	private void logging(Level level, HttpServletRequest request, String field, Exception e) {
		globalLogger.log(level, ERROR_FORMAT, request.getRequestURI(), request.getMethod(), field, e.getMessage());
	}
}
