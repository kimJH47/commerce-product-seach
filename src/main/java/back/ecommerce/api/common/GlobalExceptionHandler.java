package back.ecommerce.api.common;

import static org.slf4j.event.Level.*;

import org.slf4j.event.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import back.ecommerce.api.dto.Response;
import back.ecommerce.common.logging.GlobalLogger;
import back.ecommerce.exception.AuthenticationException;
import back.ecommerce.exception.CustomException;
import back.ecommerce.exception.ErrorCode;
import back.ecommerce.exception.InternalServerException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

	private static final String BAD_REQUEST = "잘못된 요청입니다.";
	private static final String INTERNAL_SERVER_ERROR = "요청처리중 에러가 발생했습니다.";
	private static final String AUTHENTICATION_ERROR_MESSAGE = "인증과정에서 문제가 발생 하였습니다";
	private static final String LOG_FORMAT = "{}";

	private final GlobalLogger globalLogger;

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Response> handle(AuthenticationException e) {
		logging(WARN, e);
		return Response.Failed(AUTHENTICATION_ERROR_MESSAGE, e);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleInternalServerError(Exception e) {
		logging(ERROR, e);
		return Response.Failed(INTERNAL_SERVER_ERROR, new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR));
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Response> handle(CustomException e) {
		logging(WARN, e);
		return Response.Failed(BAD_REQUEST, e);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response> handle(MethodArgumentNotValidException e, HttpServletRequest request) {
		logging(WARN, e);
		return Response.createBadRequest(BAD_REQUEST, e);
	}

	private void logging(Level level, Exception e) {
		globalLogger.log(level, LOG_FORMAT, e.getMessage());
	}
}
