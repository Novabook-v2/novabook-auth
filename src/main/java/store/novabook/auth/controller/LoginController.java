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

import store.novabook.auth.dto.LoginMemberRequest;
import store.novabook.auth.dto.TokenDto;
import store.novabook.auth.jwt.TokenProvider;

import java.util.ArrayList;
import java.util.List;

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
		String jwt = tokenProvider.createToken(authentication);

		return ResponseEntity.ok(new TokenDto(jwt));
	}
}
