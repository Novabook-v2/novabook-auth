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
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.service.AuthenticationService;

@RestController
@RequestMapping("/auth/expire")
@RequiredArgsConstructor
public class ExpireController {

	private final AuthenticationService authenticationService;
	private final TokenProvider tokenProvider;

	@PostMapping()
	ResponseEntity<IsExpireAccessTokenResponse> expire(
		@Valid @RequestBody IsExpireAccessTokenRequest isExpireAccessTokenRequest) {
		String uuid = tokenProvider.getUUID(isExpireAccessTokenRequest.accessToken());
		if (!authenticationService.existsByUuid(uuid)) {
			return ResponseEntity.ok(new IsExpireAccessTokenResponse(true));
		}
		return ResponseEntity.ok(new IsExpireAccessTokenResponse(false));
	}
}
