package store.novabook.auth.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import store.novabook.auth.exception.ErrorCode;
import store.novabook.auth.response.ErrorResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(BadCredentialsException exception) {
		log.error("BadCredentialsException: {} | Location: {}", exception.getMessage(), getLocation(exception),
			exception);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.from(ErrorCode.CANNOT_LOGIN));
	}

	private String getLocation(Throwable exception) {
		StackTraceElement element = exception.getStackTrace()[0];
		return String.format("%s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(),
			element.getLineNumber());
	}
}
