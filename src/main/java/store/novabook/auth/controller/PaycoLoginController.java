package store.novabook.auth.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.GetPaycoMembersRequest;
import store.novabook.auth.dto.response.GetPaycoMembersResponse;
import store.novabook.auth.dto.request.PaycoLoginRequest;
import store.novabook.auth.dto.response.PaycoLoginResponse;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.response.ApiResponse;
import store.novabook.auth.service.CustomMembersDetailsClient;

@RestController
@RequiredArgsConstructor()
@RequestMapping("/auth/payco")
public class PaycoLoginController {

	private final TokenProvider tokenProvider;
	private final CustomMembersDetailsClient customMembersDetailsClient;

	@PostMapping
	public ResponseEntity<PaycoLoginResponse> paycoLogin(@Valid @RequestBody PaycoLoginRequest paycoLoginRequest) {
		GetPaycoMembersRequest getPaycoMembersRequest = new GetPaycoMembersRequest(paycoLoginRequest.paycoId());
		ApiResponse<GetPaycoMembersResponse> paycoMembers = customMembersDetailsClient.getPaycoMembers(
			getPaycoMembersRequest);
		if (paycoMembers.getBody() == null) {
			return ResponseEntity.ok().build();
		}

		UUID uuid = UUID.randomUUID();
		String access = tokenProvider.createOauthAccessToken(uuid);
		String refresh = tokenProvider.createOauthRefreshToken(paycoMembers.getBody(), uuid);

		PaycoLoginResponse paycoLoginResponse = new PaycoLoginResponse(access, refresh);

		return ResponseEntity.ok().body(paycoLoginResponse);
	}
}
