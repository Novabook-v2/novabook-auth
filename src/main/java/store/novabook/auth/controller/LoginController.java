package store.novabook.auth.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.LoginMemberRequest;
import store.novabook.auth.dto.TokenDto;
import store.novabook.auth.jwt.TokenProvider;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	@PostMapping("/login")
	public ResponseEntity<TokenDto> authorize(@RequestBody LoginMemberRequest loginMemberRequest) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginMemberRequest.loginId(), loginMemberRequest.loginPassword(),
				authorities);

		// authenticate 메소드가 실행이 될 때 CustomUserDetailsService class의 loadUserByUsername 메소드가 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		// 해당 객체를 SecurityContextHolder에 저장하고
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
		String jwt = tokenProvider.createToken(authentication);

		HttpHeaders httpHeaders = new HttpHeaders();
		// response header에 jwt token에 넣어줌
		// httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

		// tokenDto를 이용해 response body에도 넣어서 리턴

		return ResponseEntity.ok().body(new TokenDto(jwt));
		// return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
	}
}
