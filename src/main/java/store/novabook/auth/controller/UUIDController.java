package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.GetMembersUUIDRequest;
import store.novabook.auth.dto.response.GetDormantMembersUUIDResponse;
import store.novabook.auth.dto.response.GetMembersTokenResponse;
import store.novabook.auth.dto.response.GetMembersUUIDResponse;
import store.novabook.auth.service.UUIDService;

@RestController
@RequestMapping("/auth/members")
@RequiredArgsConstructor
public class UUIDController {

	private final UUIDService uuidService;

	@PostMapping("/uuid")
	public ResponseEntity<GetMembersUUIDResponse> getMembersId(
		@Valid @RequestBody GetMembersUUIDRequest getMembersUuidRequest) {
		return ResponseEntity.ok(uuidService.getMembersUUID(getMembersUuidRequest));
	}

	@PostMapping("/uuid/dormant")
	public ResponseEntity<GetDormantMembersUUIDResponse> getDormantMembersId(
		@Valid @RequestBody GetMembersUUIDRequest getMembersUuidRequest) {
		return ResponseEntity.ok(uuidService.getDormantMembersId(getMembersUuidRequest));
	}

	@PostMapping("/token")
	public ResponseEntity<GetMembersTokenResponse> membersToken(HttpServletRequest request) {
		String accessToken = request.getHeader("authorization").replace("Bearer ", "");
		String refreshToken = request.getHeader("refresh").replace("Bearer ", "");

		if (accessToken.isEmpty() || refreshToken.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(uuidService.getMembersToken(accessToken, refreshToken));
	}
}
