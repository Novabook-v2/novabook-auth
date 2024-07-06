package store.novabook.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;

import store.novabook.auth.dto.LoginMembersRequest;
import store.novabook.auth.dto.LoginMembersResponse;
import store.novabook.auth.jwt.TokenProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class LoginController {

	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;

	@Autowired
	public LoginController(@Qualifier("memberAuthenticationManager") AuthenticationManager authenticationManager,
		TokenProvider tokenProvider) {
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
	}

	@PostMapping("/auth/login")
	public ResponseEntity<LoginMembersResponse> login(@RequestBody LoginMembersRequest loginMembersRequest) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginMembersRequest.loginId(), loginMembersRequest.loginPassword(),
				authorities);

		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		UUID uuid = UUID.randomUUID();
		String access = tokenProvider.createAccessToken(authentication, uuid);
		String refresh = tokenProvider.createRefreshToken(authentication, uuid);

		LoginMembersResponse loginMembersResponse = new LoginMembersResponse(access, refresh);

		return ResponseEntity.ok().body(loginMembersResponse);
	}
}
