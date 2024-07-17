package store.novabook.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.dto.request.GetDormantMembersRequest;
import store.novabook.auth.dto.request.GetMembersStatusRequest;
import store.novabook.auth.dto.request.GetNewTokenRequest;
import store.novabook.auth.dto.request.IsExpireAccessTokenRequest;
import store.novabook.auth.dto.request.LoginMembersRequest;
import store.novabook.auth.dto.response.GetDormantMembersResponse;
import store.novabook.auth.dto.response.GetMembersStatusResponse;
import store.novabook.auth.dto.response.GetNewTokenResponse;
import store.novabook.auth.dto.response.IsExpireAccessTokenResponse;
import store.novabook.auth.dto.response.LoginMembersResponse;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.DormantMembers;
import store.novabook.auth.entity.RefreshTokenInfo;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.response.ApiResponse;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;
	private final TokenService tokenService;
	private final CustomMembersDetailsClient customMembersDetailsClient;

	public LoginMembersResponse login(LoginMembersRequest loginMembersRequest) {

		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginMembersRequest.loginId(), loginMembersRequest.loginPassword(),
				null);

		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		CustomUserDetails principal = (CustomUserDetails)authentication.getPrincipal();

		RefreshTokenInfo refreshTokenInfo = tokenService.createRefreshTokenInfo(principal);
		AccessTokenInfo accessTokenInfo = tokenService.createAccessTokenInfo(principal, refreshTokenInfo);

		String accessToken = tokenProvider.createAccessToken(UUID.fromString(accessTokenInfo.getUuid()));
		String refreshToken = tokenProvider.createRefreshToken(UUID.fromString(refreshTokenInfo.getUuid()));

		tokenService.saveTokens(accessTokenInfo, refreshTokenInfo);

		return new LoginMembersResponse(accessToken, refreshToken);
	}

	public boolean logout(String accessToken) {
		try {
			String uuid = tokenProvider.getUUID(accessToken);
			if (!tokenService.existsByUuid(uuid)) {
				return false;
			}
			tokenService.deleteAllTokensByAccessToken(uuid);
		} catch (ExpiredJwtException e) {
			throw new JwtException("Expired token");
		} catch (JwtException e) {
			throw new JwtException("Invalid token");
		}
		return true;
	}

	public GetNewTokenResponse createNewToken(GetNewTokenRequest getNewTokenRequest) {
		String refreshToken = getNewTokenRequest.refreshToken().replace("Bearer ", "");
		String uuid = tokenProvider.getUUID(refreshToken);
		RefreshTokenInfo refreshTokenInfo = tokenService.getRefreshToken(uuid);
		if (Objects.isNull(refreshTokenInfo)) {
			return new GetNewTokenResponse("expired");
		}

		LocalDateTime expirationTime = refreshTokenInfo.getExpirationTime();
		LocalDateTime localDateTime = LocalDateTime.now();
		if (expirationTime.isBefore(localDateTime)) {
			return new GetNewTokenResponse("expired");
		}

		AccessTokenInfo accessTokenInfo = tokenService.createAccessTokenInfo(refreshTokenInfo);

		tokenService.changeAccessToken(refreshTokenInfo, accessTokenInfo);

		return new GetNewTokenResponse(tokenProvider.createAccessTokenFromRefreshToken(accessTokenInfo));
	}

	public IsExpireAccessTokenResponse isExpireAccessToken(IsExpireAccessTokenRequest isExpireAccessTokenRequest) {
		try {
			String uuid = tokenProvider.getUUID(isExpireAccessTokenRequest.accessToken());
			if (!tokenService.existsByUuid(uuid)) {
				return new IsExpireAccessTokenResponse(true);
			}
		} catch (ExpiredJwtException e) {
			throw new JwtException("Expired token");
		} catch (JwtException e) {
			throw new JwtException("Invalid token");
		}
		return new IsExpireAccessTokenResponse(false);
	}

	public GetMembersStatusResponse getMembersStatus(GetMembersStatusRequest getMembersStatusRequest) {
		String uuid = tokenProvider.getUUID(getMembersStatusRequest.accessToken());
		AccessTokenInfo accessTokenInfo = tokenService.getAccessToken(uuid);
		long membersId = accessTokenInfo.getMembersId();
		GetDormantMembersRequest getDormantMembersRequest = new GetDormantMembersRequest(membersId);

		ApiResponse<GetDormantMembersResponse> getDormantMembersResponse = customMembersDetailsClient.getMemberDormantStatus(
			getDormantMembersRequest);
		if (getDormantMembersResponse == null || getDormantMembersResponse.getBody() == null) {
			return new GetMembersStatusResponse(0L, null);
		}
		if (getDormantMembersResponse.getBody().memberStatusId() == 2) {
			DormantMembers dormantMembers = DormantMembers.of(uuid, membersId);
			tokenService.saveDormant(dormantMembers);
			return new GetMembersStatusResponse(getDormantMembersResponse.getBody().memberStatusId(), uuid);
		}
		if (getDormantMembersResponse.getBody().memberStatusId() == 3) {
			tokenService.deleteAllTokensByAccessToken(uuid);
			return new GetMembersStatusResponse(getDormantMembersResponse.getBody().memberStatusId(), null);
		}
		return new GetMembersStatusResponse(getDormantMembersResponse.getBody().memberStatusId(), null);
	}

}
