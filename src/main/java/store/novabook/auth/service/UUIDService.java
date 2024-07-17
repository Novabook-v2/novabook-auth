package store.novabook.auth.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.GetMembersUUIDRequest;
import store.novabook.auth.dto.response.GetDormantMembersUUIDResponse;
import store.novabook.auth.dto.response.GetMembersTokenResponse;
import store.novabook.auth.dto.response.GetMembersUUIDResponse;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.jwt.TokenProvider;

@Service
@RequiredArgsConstructor
public class UUIDService {

	private final TokenService tokenService;
	private final TokenProvider tokenProvider;

	public GetMembersUUIDResponse getMembersUUID(GetMembersUUIDRequest getMembersUUIDRequest) {
		AccessTokenInfo accessTokenInfo = tokenService.getAccessToken(getMembersUUIDRequest.uuid());
		return new GetMembersUUIDResponse(
			accessTokenInfo.getMembersId(), accessTokenInfo.getRole());
	}

	public GetDormantMembersUUIDResponse getDormantMembersId(GetMembersUUIDRequest getMembersUUIDRequest) {
		return new GetDormantMembersUUIDResponse(
			tokenService.getDormant(getMembersUUIDRequest.uuid()).getMembersId());
	}

	public GetMembersTokenResponse getMembersToken(String accessToken, String refreshToken) {

		String uuid = null;
		if (tokenProvider.validateToken(accessToken)) {
			uuid = tokenProvider.getUUID(accessToken);
		} else {
			uuid = tokenProvider.getUUID(refreshToken);
		}

		return new GetMembersTokenResponse(
			tokenService.getAccessToken(uuid).getMembersId());
	}
}
