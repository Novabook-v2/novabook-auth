package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.IsExpireAccessTokenRequest;
import store.novabook.auth.dto.response.IsExpireAccessTokenResponse;
import store.novabook.auth.service.AuthenticationService;

@RestController
@RequestMapping("/auth/expire")
@RequiredArgsConstructor
public class ExpireController {

	private final AuthenticationService authenticationService;

	@PostMapping()
	ResponseEntity<IsExpireAccessTokenResponse> expire(
		@Valid @RequestBody IsExpireAccessTokenRequest isExpireAccessTokenRequest) {
		return ResponseEntity.ok(authenticationService.isExpireAccessToken(isExpireAccessTokenRequest));
	}
}
