package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.LinkPaycoMembersRequest;
import store.novabook.auth.dto.LinkPaycoMembersUUIDRequest;
import store.novabook.auth.entity.AuthenticationInfo;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.response.ApiResponse;
import store.novabook.auth.service.AuthenticationService;
import store.novabook.auth.service.CustomMembersDetailsClient;

@RestController
@RequiredArgsConstructor()
@RequestMapping("/auth/payco/link")
public class PaycoLinkController {
	private final CustomMembersDetailsClient customMembersDetailsClient;
	private final AuthenticationService authenticationService;
	private final TokenProvider tokenProvider;

	@PostMapping
	public ResponseEntity<Void> paycoLink(@Valid @RequestBody LinkPaycoMembersUUIDRequest linkPaycoMembersUUIDRequest) {

		String uuid = tokenProvider.getUsernameFromToken(linkPaycoMembersUUIDRequest.accessToken());

		AuthenticationInfo auth = authenticationService.getAuth(uuid);

		LinkPaycoMembersRequest linkPaycoMembersRequest = new LinkPaycoMembersRequest(auth.getMembersId(), linkPaycoMembersUUIDRequest.oauthId());
		ApiResponse<Void> voidApiResponse = customMembersDetailsClient.linkPayco(linkPaycoMembersRequest);

		return ResponseEntity.ok().build();
	}
}
