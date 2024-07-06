package store.novabook.auth.controller;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.GetPaycoMembersRequest;
import store.novabook.auth.dto.GetPaycoMembersResponse;
import store.novabook.auth.dto.PaycoLoginRequest;
import store.novabook.auth.dto.PaycoLoginResponse;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.response.ApiResponse;
import store.novabook.auth.service.CustomMembersDetailClient;

@RestController
@RequiredArgsConstructor()
@RequestMapping("/auth/payco")
public class PaycoLoginController {

	private final TokenProvider tokenProvider;
	private final CustomMembersDetailClient customMembersDetailClient;

	@PostMapping
	public ResponseEntity<PaycoLoginResponse> paycoLogin(@Valid @RequestBody PaycoLoginRequest paycoLoginRequest) {
		GetPaycoMembersRequest getPaycoMembersRequest = new GetPaycoMembersRequest(paycoLoginRequest.paycoId());
		ApiResponse<GetPaycoMembersResponse> paycoMembers = customMembersDetailClient.getPaycoMembers(
			getPaycoMembersRequest);
		if (paycoMembers.getBody() == null) {
			return ResponseEntity.ok().build();
		}

		UUID uuid = UUID.randomUUID();
		String access = tokenProvider.createAccessToken(uuid);
		String refresh = tokenProvider.createRefreshToken(paycoMembers.getBody(), uuid);

		PaycoLoginResponse paycoLoginResponse = new PaycoLoginResponse(access, refresh);

		return ResponseEntity.ok().body(paycoLoginResponse);
	}
}
