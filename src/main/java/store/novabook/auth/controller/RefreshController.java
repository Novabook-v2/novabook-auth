package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.GetNewTokenRequest;
import store.novabook.auth.dto.response.GetNewTokenResponse;
import store.novabook.auth.service.AuthenticationService;

@RestController
@RequestMapping("/auth/refresh")
@RequiredArgsConstructor
public class RefreshController {

	private final AuthenticationService authenticationService;

	@PostMapping
	public ResponseEntity<GetNewTokenResponse> getNewToken(@Valid @RequestBody GetNewTokenRequest getNewTokenRequest) {
		GetNewTokenResponse newToken = authenticationService.createNewToken(getNewTokenRequest);
		return ResponseEntity.ok(newToken);
	}
}
