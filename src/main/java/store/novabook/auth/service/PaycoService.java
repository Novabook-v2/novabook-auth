package store.novabook.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.GetPaycoMembersRequest;
import store.novabook.auth.dto.request.LinkPaycoMembersRequest;
import store.novabook.auth.dto.request.LinkPaycoMembersUUIDRequest;
import store.novabook.auth.dto.request.PaycoLoginRequest;
import store.novabook.auth.dto.response.GetPaycoMembersResponse;
import store.novabook.auth.dto.response.PaycoLoginResponse;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.RefreshTokenInfo;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.response.ApiResponse;

@Service
@RequiredArgsConstructor
public class PaycoService {

	private final CustomMembersDetailsClient customMembersDetailsClient;
	private final TokenService tokenService;
	private final TokenProvider tokenProvider;

	public boolean paycoLink(LinkPaycoMembersUUIDRequest linkPaycoMembersUUIDRequest) {
		String uuid = tokenProvider.getUUID(linkPaycoMembersUUIDRequest.accessToken());

		AccessTokenInfo accessTokenInfo = tokenService.getAccessToken(uuid);

		LinkPaycoMembersRequest linkPaycoMembersRequest = new LinkPaycoMembersRequest(accessTokenInfo.getMembersId(),
			linkPaycoMembersUUIDRequest.oauthId());
		ApiResponse<Void> apiResponse = customMembersDetailsClient.linkPayco(linkPaycoMembersRequest);
		return apiResponse.getHeader().get("resultMessage").equals("SUCCESS");
	}

	public PaycoLoginResponse paycoLogin(PaycoLoginRequest paycoLoginRequest) {

		GetPaycoMembersRequest getPaycoMembersRequest = new GetPaycoMembersRequest(paycoLoginRequest.paycoId());
		ApiResponse<GetPaycoMembersResponse> paycoMembers = customMembersDetailsClient.getPaycoMembers(
			getPaycoMembersRequest);
		if (paycoMembers.getBody() == null) {
			return null;
		}

		RefreshTokenInfo paycoRefreshTokenInfo = tokenService.createPaycoRefreshTokenInfo(paycoMembers.getBody().id());
		AccessTokenInfo paycoAccessTokenInfo = tokenService.createPaycoAccessTokenInfo(paycoMembers.getBody().id(),
			paycoRefreshTokenInfo);

		String accessToken = tokenProvider.createAccessToken(UUID.fromString(paycoRefreshTokenInfo.getUuid()));
		String refreshToken = tokenProvider.createRefreshToken(UUID.fromString(paycoAccessTokenInfo.getUuid()));

		tokenService.saveTokens(paycoAccessTokenInfo, paycoRefreshTokenInfo);

		return new PaycoLoginResponse(accessToken, refreshToken);
	}
}
