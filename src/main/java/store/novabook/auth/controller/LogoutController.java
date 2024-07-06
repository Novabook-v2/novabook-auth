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

@RestController
@RequestMapping("/auth/logout")
@RequiredArgsConstructor
public class LogoutController {

	private final AuthenticationService authenticationService;
	private final TokenProvider tokenProvider;

	@PostMapping
	public ResponseEntity<Void> logout(HttpServletRequest request) {

		String refreshToken = request.getHeader("refresh").replace("Bearer ", "");

		if (Objects.isNull(refreshToken)) {
			return ResponseEntity.badRequest().build();
		}

		String uuid = null;

		try {
			uuid = tokenProvider.getUsernameFromToken(refreshToken);
			if (!authenticationService.existsByUuid(uuid)) {
				return ResponseEntity.badRequest().build();
			}
			if (tokenProvider.validateToken(refreshToken)) {
				if (Boolean.TRUE.equals(authenticationService.deleteAuth(uuid))) {
					return ResponseEntity.ok().build();
				} else {
					return ResponseEntity.badRequest().build();
				}
			} else {
				return ResponseEntity.ok().build();
			}
		} catch (ExpiredJwtException e) {
		} catch (JwtException e) {
		}
		return ResponseEntity.ok().build();
	}
}
