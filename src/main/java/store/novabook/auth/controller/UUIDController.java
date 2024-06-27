package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.GetUsersUUIDRequest;
import store.novabook.auth.dto.GetUsersUUIDResponse;
import store.novabook.auth.service.AuthService;

@RestController
@RequestMapping("/auth/uuid")
@RequiredArgsConstructor
public class UUIDController {

	private final AuthService authService;

	@PostMapping()
	public ResponseEntity<GetUsersUUIDResponse> authorizeAdmin(@RequestBody GetUsersUUIDRequest getUsersUuidRequest) {
		GetUsersUUIDResponse getUsersUUIDResponse = new GetUsersUUIDResponse(
			Long.toString(authService.getAuth(getUsersUuidRequest.uuid()).getUsersId()));
		return ResponseEntity.ok(getUsersUUIDResponse);
	}

}
