package store.novabook.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

class PaycoServiceTest {

	@Mock
	private CustomMembersDetailsClient customMembersDetailsClient;

	@Mock
	private TokenService tokenService;

	@Mock
	private TokenProvider tokenProvider;

	@InjectMocks
	private PaycoService paycoService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testPaycoLink_Success() {
		// given
		String accessToken = "validAccessToken";
		String uuid = UUID.randomUUID().toString();
		long membersId = 1L;
		String oauthId = "paycoOauthId";

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			UUID.randomUUID().toString(),
			"refreshUuid",
			membersId,
			"ROLE_USER",
			LocalDateTime.now().plusHours(1),
			LocalDateTime.now()
		);

		ApiResponse<Void> apiResponse = ApiResponse.success(null);

		LinkPaycoMembersUUIDRequest request = new LinkPaycoMembersUUIDRequest(accessToken, oauthId);

		given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		given(tokenService.getAccessToken(uuid)).willReturn(accessTokenInfo);
		given(customMembersDetailsClient.linkPayco(any(LinkPaycoMembersRequest.class)))
			.willReturn(apiResponse);

		// when
		boolean result = paycoService.paycoLink(request);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void testPaycoLink_Failure() {
		// given
		String accessToken = "validAccessToken";
		String uuid = UUID.randomUUID().toString();
		long membersId = 1L;
		String oauthId = "paycoOauthId";

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			UUID.randomUUID().toString(),
			"refreshUuid",
			membersId,
			"ROLE_USER",
			LocalDateTime.now().plusHours(1),
			LocalDateTime.now()
		);

		ApiResponse<Void> apiResponse = ApiResponse.error(null);

		LinkPaycoMembersUUIDRequest request = new LinkPaycoMembersUUIDRequest(accessToken, oauthId);

		given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		given(tokenService.getAccessToken(uuid)).willReturn(accessTokenInfo);
		given(customMembersDetailsClient.linkPayco(any(LinkPaycoMembersRequest.class)))
			.willReturn(apiResponse);

		// when
		boolean result = paycoService.paycoLink(request);

		// then
		assertThat(result).isFalse();
	}

	@Test
	void testPaycoLogin_Success() {
		// given
		String paycoId = "paycoId";
		String accessToken = "newAccessToken";
		String refreshToken = "newRefreshToken";
		String uuidAccessToken = UUID.randomUUID().toString();
		String uuidRefreshToken = UUID.randomUUID().toString();

		GetPaycoMembersResponse paycoMembersResponse = new GetPaycoMembersResponse(1L);

		PaycoLoginRequest request = new PaycoLoginRequest(paycoId);

		RefreshTokenInfo refreshTokenInfo = new RefreshTokenInfo(
			uuidRefreshToken,
			uuidAccessToken,
			1L,
			"ROLE_USER",
			LocalDateTime.now().plusDays(7),
			LocalDateTime.now()
		);

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			uuidAccessToken,
			uuidRefreshToken,
			1L,
			"ROLE_USER",
			LocalDateTime.now().plusHours(1),
			LocalDateTime.now()
		);

		given(customMembersDetailsClient.getPaycoMembers(any(GetPaycoMembersRequest.class)))
			.willReturn(ApiResponse.success(paycoMembersResponse));
		given(tokenService.createPaycoRefreshTokenInfo(anyLong())).willReturn(refreshTokenInfo);
		given(tokenService.createPaycoAccessTokenInfo(anyLong(), any(RefreshTokenInfo.class)))
			.willReturn(accessTokenInfo);
		given(tokenProvider.createAccessToken(any(UUID.class))).willReturn(accessToken);
		given(tokenProvider.createRefreshToken(any(UUID.class))).willReturn(refreshToken);

		// when
		PaycoLoginResponse response = paycoService.paycoLogin(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.accessToken()).isEqualTo(accessToken);
		assertThat(response.refreshToken()).isEqualTo(refreshToken);
	}

	@Test
	void testPaycoLogin_Failure() {
		// given
		String paycoId = "paycoId";
		PaycoLoginRequest request = new PaycoLoginRequest(paycoId);

		given(customMembersDetailsClient.getPaycoMembers(any(GetPaycoMembersRequest.class)))
			.willReturn(ApiResponse.error(null));

		// when
		PaycoLoginResponse response = paycoService.paycoLogin(request);

		// then
		assertThat(response).isNull();
	}
}
