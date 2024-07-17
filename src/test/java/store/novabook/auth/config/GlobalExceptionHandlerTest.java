package store.novabook.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

import store.novabook.auth.exception.ErrorCode;
import store.novabook.auth.response.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

	private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

	@Test
	void handleUnauthorizedException_ShouldReturnErrorResponse() {
		// Given
		BadCredentialsException exception = new BadCredentialsException("Invalid credentials");

		// When
		ResponseEntity<ErrorResponse> responseEntity = handler.handleBadCredentialsException(exception);

		// Then
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		ErrorResponse errorResponse = responseEntity.getBody();
		assertEquals(ErrorCode.CANNOT_LOGIN.getMessage(), errorResponse.message());
	}
}
