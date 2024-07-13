package store.novabook.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import store.novabook.auth.dto.request.LoginMembersRequest;
import store.novabook.auth.dto.response.LoginMembersResponse;
import store.novabook.auth.jwt.TokenProvider;

import java.util.UUID;

@RestController
public class LoginController {

	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;

	@Autowired
	public LoginController(AuthenticationManager authenticationManager,
		TokenProvider tokenProvider) {
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
	}

	@PostMapping("/auth/login")
	public ResponseEntity<LoginMembersResponse> login(@RequestBody LoginMembersRequest loginMembersRequest) {

		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginMembersRequest.loginId(), loginMembersRequest.loginPassword(),
				null);

		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		UUID uuid = UUID.randomUUID();
		String access = tokenProvider.createAccessToken(authentication, uuid);
		String refresh = tokenProvider.createRefreshToken(authentication, uuid);

		LoginMembersResponse loginMembersResponse = new LoginMembersResponse(access, refresh);

		return ResponseEntity.ok().body(loginMembersResponse);
	}
}
