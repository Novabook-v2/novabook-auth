package store.novabook.auth.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.GetNewTokenRequest;
import store.novabook.auth.dto.GetNewTokenResponse;
import store.novabook.auth.entity.AuthenticationInfo;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.service.AuthenticationService;

@RestController
@RequestMapping("/auth/refresh")
@RequiredArgsConstructor
public class RefreshController {

	private final AuthenticationService authenticationService;
	private final TokenProvider tokenProvider;

	@PostMapping
	public ResponseEntity<GetNewTokenResponse> getRefreshToken(@Valid @RequestBody GetNewTokenRequest getNewTokenRequest) {

		String refreshToken = getNewTokenRequest.refreshToken().replace("Bearer ", "");
		String uuid = tokenProvider.getUsernameFromToken(refreshToken);
		AuthenticationInfo authenticationInfo = authenticationService.getAuth(uuid);


		LocalDateTime expirationTime = authenticationInfo.getExpirationTime();
		LocalDateTime now = LocalDateTime.now();
		if (expirationTime.isBefore(now)) {
			return ResponseEntity.ok(new GetNewTokenResponse("expired"));
		}
		return ResponseEntity.ok(new GetNewTokenResponse(tokenProvider.createAccessToken(UUID.fromString(uuid))));
	}
}
