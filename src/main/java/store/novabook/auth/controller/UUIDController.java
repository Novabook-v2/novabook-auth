package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.response.GetDormantMembersUUIDResponse;
import store.novabook.auth.dto.response.GetMembersTokenResponse;
import store.novabook.auth.dto.request.GetMembersUUIDRequest;
import store.novabook.auth.dto.response.GetMembersUUIDResponse;
import store.novabook.auth.entity.AuthenticationInfo;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.service.AuthenticationService;

@RestController
@RequestMapping("/auth/members")
@RequiredArgsConstructor
public class UUIDController {

	private final AuthenticationService authenticationService;
	private final TokenProvider tokenProvider;

	@PostMapping("/uuid")
	public ResponseEntity<GetMembersUUIDResponse> uuid(@RequestBody GetMembersUUIDRequest getMembersUuidRequest) {
		AuthenticationInfo authenticationInfo = authenticationService.getAuth(getMembersUuidRequest.uuid());
		GetMembersUUIDResponse getMembersUUIDResponse = new GetMembersUUIDResponse(
			authenticationInfo.getMembersId(), authenticationInfo.getRole());
		return ResponseEntity.ok(getMembersUUIDResponse);
	}

	@PostMapping("/uuid/dormant")
	public ResponseEntity<GetDormantMembersUUIDResponse> dormantUuid(
		@RequestBody GetMembersUUIDRequest getMembersUuidRequest) {
		GetDormantMembersUUIDResponse getDormantMembersUUIDResponse = new GetDormantMembersUUIDResponse(
			authenticationService.getDormant(getMembersUuidRequest.uuid()).getMembersId());
		return ResponseEntity.ok(getDormantMembersUUIDResponse);
	}

	@PostMapping("/token")
	public ResponseEntity<GetMembersTokenResponse> token(HttpServletRequest request) {
		String accessToken = request.getHeader("authorization").replace("Bearer ", "");
		String refreshToken = request.getHeader("refresh").replace("Bearer ", "");

		if (accessToken.isEmpty() || refreshToken.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		String uuid = null;
		if (tokenProvider.validateToken(accessToken)) {
			uuid = tokenProvider.getUsernameFromToken(accessToken);
		} else {
			uuid = tokenProvider.getUsernameFromToken(refreshToken);
		}

		GetMembersTokenResponse getMembersTokenResponse = new GetMembersTokenResponse(
			authenticationService.getAuth(uuid).getMembersId());
		return ResponseEntity.ok(getMembersTokenResponse);
	}
}
