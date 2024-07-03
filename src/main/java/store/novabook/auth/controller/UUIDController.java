package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.GetMembersUUIDRequest;
import store.novabook.auth.dto.GetMembersUUIDResponse;
import store.novabook.auth.service.AuthService;

@RestController
@RequestMapping("/auth/members/uuid")
@RequiredArgsConstructor
public class UUIDController {

	private final AuthService authService;

	@PostMapping()
	public ResponseEntity<GetMembersUUIDResponse> uuid(@RequestBody GetMembersUUIDRequest getMembersUuidRequest) {
		GetMembersUUIDResponse getMembersUUIDResponse = new GetMembersUUIDResponse(
			Long.toString(authService.getAuth(getMembersUuidRequest.uuid()).getUsersId()));
		return ResponseEntity.ok(getMembersUUIDResponse);
	}

}
