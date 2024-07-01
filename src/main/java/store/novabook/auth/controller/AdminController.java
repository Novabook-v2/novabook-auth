package store.novabook.auth.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.novabook.auth.dto.LoginMemberRequest;
import store.novabook.auth.dto.TokenDto;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.service.CustomAdminDetailsService;

@RestController
@RequestMapping("/auth/admin")
public class AdminController {

	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;
	private final CustomAdminDetailsService customAdminDetailsService;

	@Autowired
	public AdminController(CustomAdminDetailsService customAdminDetailsService,
		@Qualifier("adminAuthenticationManager") AuthenticationManager authenticationManager,
		TokenProvider tokenProvider) {
		this.customAdminDetailsService = customAdminDetailsService;
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<TokenDto> authorizeAdmin(@RequestBody LoginMemberRequest loginMemberRequest) {
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginMemberRequest.loginId(), loginMemberRequest.loginPassword());

		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UUID uuid = UUID.randomUUID();
		String access = tokenProvider.createAccessToken(authentication, uuid);
		String refresh = tokenProvider.createRefreshToken(authentication, uuid);

		return ResponseEntity.ok(new TokenDto(access));
	}
}