package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.GetMembersStatusRequest;
import store.novabook.auth.dto.response.GetMembersStatusResponse;
import store.novabook.auth.service.AuthenticationService;

@RestController
@RequestMapping("/auth/members/status")
@RequiredArgsConstructor
public class StatusController {

	private final AuthenticationService authenticationService;

	@PostMapping
	public ResponseEntity<GetMembersStatusResponse> status(
		@Valid @RequestBody GetMembersStatusRequest getMembersStatusRequest) {
		return ResponseEntity.ok(authenticationService.getMembersStatus(getMembersStatusRequest));
	}
}
