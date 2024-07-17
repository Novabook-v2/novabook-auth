package store.novabook.auth.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.service.AuthenticationService;

@RestController
@RequestMapping("/auth/logout")
@RequiredArgsConstructor
public class LogoutController {

	private final AuthenticationService authenticationService;

	@PostMapping
	public ResponseEntity<Void> logout(HttpServletRequest request) {

		String accessToken = request.getHeader("authorization");
		if (Objects.isNull(accessToken)) {
			return ResponseEntity.badRequest().build();
		}
		accessToken = accessToken.replace("Bearer ", "");

		if (authenticationService.logout(accessToken)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.badRequest().build();
		}

	}
}
