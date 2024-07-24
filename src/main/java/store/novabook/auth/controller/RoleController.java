package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.GetMembersRoleRequest;
import store.novabook.auth.dto.response.GetMembersRoleResponse;
import store.novabook.auth.service.AuthenticationService;

@RestController
@RequestMapping("/auth/role")
@RequiredArgsConstructor
public class RoleController {

	private final AuthenticationService authenticationService;
	@PostMapping
	public ResponseEntity<GetMembersRoleResponse> getRole(@Valid @RequestBody GetMembersRoleRequest getMembersRoleRequest) {
		return ResponseEntity.ok(authenticationService.getMembersRole(getMembersRoleRequest.accessToken()));
	}
}
