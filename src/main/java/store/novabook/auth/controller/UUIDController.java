package store.novabook.auth.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.GetMembersTokenResponse;
import store.novabook.auth.dto.GetMembersUUIDRequest;
import store.novabook.auth.dto.GetMembersUUIDResponse;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.service.AuthService;

@RestController
@RequestMapping("/auth/members")
@RequiredArgsConstructor
public class UUIDController {

	private final AuthService authService;
	private final TokenProvider tokenProvider;

	@PostMapping("/uuid")
	public ResponseEntity<GetMembersUUIDResponse> uuid(@RequestBody GetMembersUUIDRequest getMembersUuidRequest) {
		GetMembersUUIDResponse getMembersUUIDResponse = new GetMembersUUIDResponse(
			Long.toString(authService.getAuth(getMembersUuidRequest.uuid()).getUsersId()));
		return ResponseEntity.ok(getMembersUUIDResponse);
	}

	@PostMapping("/token")
	public ResponseEntity<GetMembersTokenResponse> token(HttpServletRequest request) {
		String accessToken = request.getHeader("authorization").replace("Bearer ", "");
		String refreshToken = request.getHeader("refresh").replace("Bearer ", "");

		if (Objects.isNull(accessToken) || Objects.isNull(refreshToken)) {
			return ResponseEntity.badRequest().build();
		}

		String uuid = null;
		try {
			if (tokenProvider.validateToken(accessToken)) {
				uuid = tokenProvider.getUsernameFromToken(accessToken);
			} else {
				uuid = tokenProvider.getUsernameFromToken(refreshToken);
			}
		} catch (Exception e) {
		}
		GetMembersTokenResponse getMembersTokenResponse = new GetMembersTokenResponse(
			authService.getAuth(uuid).getUsersId());
		return ResponseEntity.ok(getMembersTokenResponse);
	}
}
