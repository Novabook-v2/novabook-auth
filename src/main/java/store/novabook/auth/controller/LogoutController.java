package store.novabook.auth.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.service.AuthenticationService;
import store.novabook.auth.service.JWTTokenService;

@RestController
@RequestMapping("/auth/logout")
@RequiredArgsConstructor
public class LogoutController {

	private final JWTTokenService jwtTokenService;
	private final AuthenticationService authenticationService;
	private final TokenProvider tokenProvider;

	@PostMapping
	public ResponseEntity<Void> logout(HttpServletRequest request) {

		String accessToken = request.getHeader("access").replace("Bearer ", "");

		if (Objects.isNull(accessToken)) {
			return ResponseEntity.badRequest().build();
		}

		String uuid = null;

		try {
			uuid = tokenProvider.getUUID(accessToken);
			if (!authenticationService.existsByUuid(uuid)) {
				return ResponseEntity.badRequest().build();
			}
			authenticationService.deleteAccessToken(uuid);
		} catch (ExpiredJwtException e) {
		} catch (JwtException e) {
		}
		return ResponseEntity.ok().build();
	}
}
