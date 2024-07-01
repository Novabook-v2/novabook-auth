package store.novabook.auth.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.GetNewTokenRequest;
import store.novabook.auth.dto.GetNewTokenResponse;
import store.novabook.auth.dto.GetUsersUUIDResponse;
import store.novabook.auth.entity.Auth;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.service.AuthService;

@RestController
@RequestMapping("/auth/refresh")
@RequiredArgsConstructor
public class RefreshController {

	private final AuthService authService;
	private final TokenProvider tokenProvider;
	private final AuthenticationManager authenticationManager;

	@PostMapping
	public ResponseEntity<GetNewTokenResponse> getRefreshToken(@Valid @RequestBody GetNewTokenRequest getNewTokenRequest) {

		// String token = authorization.split(" ")[1];
		String refreshToken = getNewTokenRequest.refreshToken().split(" ")[1];
		String uuid = tokenProvider.getUsernameFromToken(refreshToken);
		Auth auth = authService.getAuth(uuid);

		LocalDateTime expirationTime = auth.getExpirationTime();
		LocalDateTime now = LocalDateTime.now();
		String accessToken = null;
		if (now.isAfter(expirationTime)) {
			// 만료 시간이 지났을 때의 처리를 여기에 작성합니다.
			// 예를 들어, 새로운 토큰을 발급하거나, 사용자에게 만료 알림을 보내는 등의 작업을 수행할 수 있습니다.
			// List<SimpleGrantedAuthority> authorities = new ArrayList<>();
			// authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			// UsernamePasswordAuthenticationToken authenticationToken =
			// 	new UsernamePasswordAuthenticationToken(null, null,
			// 		authorities);
			//
			// Authentication authentication = authenticationManager.authenticate(authenticationToken);
			accessToken = tokenProvider.createAccessToken(UUID.fromString(uuid));

		}
		return ResponseEntity.ok(new GetNewTokenResponse(accessToken));
	}
}
