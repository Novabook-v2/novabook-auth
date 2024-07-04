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

import store.novabook.auth.dto.LoginMemberRequest;
import store.novabook.auth.dto.TokenDto;
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
	public ResponseEntity<TokenDto> authorize(@RequestBody LoginMemberRequest loginMemberRequest) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginMemberRequest.loginId(), loginMemberRequest.loginPassword(),
				authorities);

		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UUID uuid = UUID.randomUUID();
		String access = tokenProvider.createAccessToken(authentication, uuid);
		String refresh = tokenProvider.createRefreshToken(authentication, uuid);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + access);
		headers.set("Refresh", "Bearer " + refresh);

		return ResponseEntity.ok().headers(headers).body(new TokenDto(access));
	}
}
