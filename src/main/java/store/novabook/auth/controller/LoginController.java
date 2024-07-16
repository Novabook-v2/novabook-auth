package store.novabook.auth.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.dto.request.LoginMembersRequest;
import store.novabook.auth.dto.response.LoginMembersResponse;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.RefreshTokenInfo;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.service.AuthenticationService;
import store.novabook.auth.service.JWTTokenService;
import store.novabook.auth.service.TokenService;

@RestController
@RequiredArgsConstructor
public class LoginController {

	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;
	private final AuthenticationService authenticationService;

	@PostMapping("/auth/login")
	public ResponseEntity<LoginMembersResponse> login(@Valid @RequestBody LoginMembersRequest loginMembersRequest) {

		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginMembersRequest.loginId(), loginMembersRequest.loginPassword(),
				null);

		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		CustomUserDetails principal = (CustomUserDetails)authentication.getPrincipal();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		String authoritiesString = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		Date now = new Date();
		Date refreshValidity = new Date(now.getTime() + 60000 * 1000);
		Date accessValidity = new Date(now.getTime() + 60 * 1000);

		String accessTokenUUID = UUID.randomUUID().toString();
		String refreshTokenUUID = UUID.randomUUID().toString();

		RefreshTokenInfo refreshTokenInfo = RefreshTokenInfo.of(refreshTokenUUID, accessTokenUUID,
			principal.getMembersId(), authoritiesString,
			LocalDateTime.ofInstant(refreshValidity.toInstant(), ZoneId.systemDefault()));

		AccessTokenInfo accessTokenInfo = AccessTokenInfo.of(accessTokenUUID, refreshTokenUUID,
			principal.getMembersId(), authoritiesString,
			LocalDateTime.ofInstant(accessValidity.toInstant(), ZoneId.systemDefault()));

		String accessToken = tokenProvider.createAccessToken(UUID.fromString(accessTokenInfo.getUuid()));
		String refreshToken = tokenProvider.createRefreshToken(UUID.fromString(refreshTokenInfo.getUuid()));

		authenticationService.saveTokens(accessTokenInfo, refreshTokenInfo);

		LoginMembersResponse loginMembersResponse = new LoginMembersResponse(accessToken, refreshToken);

		return ResponseEntity.ok().body(loginMembersResponse);
	}
}
