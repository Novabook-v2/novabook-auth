package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.LoginMembersRequest;
import store.novabook.auth.dto.response.LoginMembersResponse;
import store.novabook.auth.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
public class LoginController {

	private final AuthenticationService authenticationService;

	@PostMapping("/auth/login")
	public ResponseEntity<LoginMembersResponse> login(@Valid @RequestBody LoginMembersRequest loginMembersRequest) {
		return ResponseEntity.ok().body(authenticationService.login(loginMembersRequest));
	}
}
