package store.novabook.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
import store.novabook.auth.entity.AuthenticationMembers;
import store.novabook.auth.entity.DormantMembers;
import store.novabook.auth.entity.RefreshTokenInfo;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.response.ApiResponse;

class AuthenticationServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private TokenService tokenService;

	@Mock
	private CustomMembersDetailsClient customMembersDetailsClient;

	@InjectMocks
	private AuthenticationService authenticationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testLogin() {
		// given
		String loginId = "testUser";
		String loginPassword = "testPassword";
		LoginMembersRequest loginMembersRequest = new LoginMembersRequest(loginId, loginPassword);

		AuthenticationMembers authenticationMembers = AuthenticationMembers.of(
			3L,
			"1",
			"12",
			"ROLE_MEMBERS"
		);

		CustomUserDetails customUserDetails = new CustomUserDetails(authenticationMembers);
		Authentication authentication = mock(Authentication.class);

		RefreshTokenInfo refreshTokenInfo = new RefreshTokenInfo(
			UUID.randomUUID().toString(),
			"accessTokenUUID",
			1L,
			"ROLE_MEMBERS",
			LocalDateTime.now().plusDays(7),
			LocalDateTime.now()
		);

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			UUID.randomUUID().toString(),
			"refreshUuid",
			1L,
			"ROLE_MEMBERS",
			LocalDateTime.now().plusHours(1),
			LocalDateTime.now()
		);

		String accessToken = "accessToken";
		String refreshToken = "refreshToken";

		given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.willReturn(authentication);
		given(authentication.getPrincipal()).willReturn(customUserDetails);
		given(tokenService.createRefreshTokenInfo(any(CustomUserDetails.class)))
			.willReturn(refreshTokenInfo);
		given(tokenService.createAccessTokenInfo(any(CustomUserDetails.class), eq(refreshTokenInfo)))
			.willReturn(accessTokenInfo);
		given(tokenProvider.createAccessToken(UUID.fromString(accessTokenInfo.getUuid())))
			.willReturn(accessToken);
		given(tokenProvider.createRefreshToken(UUID.fromString(refreshTokenInfo.getUuid())))
			.willReturn(refreshToken);

		// when
		LoginMembersResponse response = authenticationService.login(loginMembersRequest);

		// then
		assertThat(response.accessToken()).isEqualTo(accessToken);
		assertThat(response.refreshToken()).isEqualTo(refreshToken);

		verify(tokenService).saveTokens(accessTokenInfo, refreshTokenInfo);
	}

	 @Test
	 void testLogoutSuccess() {
		 // given
		 String accessToken = "validAccessToken";
		 String uuid = "validUuid";

		 given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		 given(tokenService.existsByUuid(uuid)).willReturn(true);

		 // when
		 boolean result = authenticationService.logout(accessToken);

		 // then
		 assertThat(result).isTrue();
		 verify(tokenService).deleteAllTokensByAccessToken(uuid);
	 }

	 @Test
	 void testLogoutTokenNotExists() {
		 // given
		 String accessToken = "validAccessToken";
		 String uuid = "validUuid";

		 given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		 given(tokenService.existsByUuid(uuid)).willReturn(false);

		 // when
		 boolean result = authenticationService.logout(accessToken);

		 // then
		 assertThat(result).isFalse();
	 }

	 @Test
	 void testLogoutExpiredToken() {
		 // given
		 String accessToken = "expiredAccessToken";

		 given(tokenProvider.getUUID(accessToken)).willThrow(new ExpiredJwtException(null, null, "Expired token"));

		 // when/then
		 try {
			 authenticationService.logout(accessToken);
		 } catch (JwtException e) {
			 assertThat(e).isInstanceOf(JwtException.class).hasMessage("Expired token");
		 }
	 }

	 @Test
	 void testLogoutInvalidToken() {
		 // given
		 String accessToken = "invalidAccessToken";

		 given(tokenProvider.getUUID(accessToken)).willThrow(new JwtException("Invalid token"));

		 // when/then
		 try {
			 authenticationService.logout(accessToken);
		 } catch (JwtException e) {
			 assertThat(e).isInstanceOf(JwtException.class).hasMessage("Invalid token");
		 }
	 }

	 @Test
	 void testCreateNewTokenSuccess() {
		 // given
		 String refreshToken = "Bearer validRefreshToken";
		 String uuid = "validUuid";
		 String newAccessToken = "newAccessToken";

		 GetNewTokenRequest getNewTokenRequest = new GetNewTokenRequest(refreshToken);
		 RefreshTokenInfo refreshTokenInfo = new RefreshTokenInfo(
			 uuid,
			 "accessTokenUUID",
			 1L,
			 "ROLE_MEMBERS",
			 LocalDateTime.now().plusDays(7),
			 LocalDateTime.now()
		 );

		 AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			 UUID.randomUUID().toString(),
			 uuid,
			 1L,
			 "ROLE_MEMBERS",
			 LocalDateTime.now().plusHours(1),
			 LocalDateTime.now()
		 );

		 given(tokenProvider.getUUID(refreshToken.replace("Bearer ", ""))).willReturn(uuid);
		 given(tokenService.getRefreshToken(uuid)).willReturn(refreshTokenInfo);
		 given(tokenService.createAccessTokenInfo(refreshTokenInfo)).willReturn(accessTokenInfo);
		 given(tokenProvider.createAccessTokenFromRefreshToken(accessTokenInfo)).willReturn(newAccessToken);

		 // when
		 GetNewTokenResponse response = authenticationService.createNewToken(getNewTokenRequest);

		 // then
		 assertThat(response.accessToken()).isEqualTo(newAccessToken);
		 verify(tokenService).changeAccessToken(refreshTokenInfo, accessTokenInfo);
	 }

	 @Test
	 void testCreateNewTokenExpired() {
		 // given
		 String refreshToken = "Bearer validRefreshToken";
		 String uuid = "validUuid";

		 GetNewTokenRequest getNewTokenRequest = new GetNewTokenRequest(refreshToken);
		 RefreshTokenInfo refreshTokenInfo = new RefreshTokenInfo(
			 uuid,
			 "accessTokenUUID",
			 1L,
			 "ROLE_MEMBERS",
			 LocalDateTime.now().minusDays(1),
			 LocalDateTime.now().minusDays(2)
		 );

		 given(tokenProvider.getUUID(refreshToken.replace("Bearer ", ""))).willReturn(uuid);
		 given(tokenService.getRefreshToken(uuid)).willReturn(refreshTokenInfo);

		 // when
		 GetNewTokenResponse response = authenticationService.createNewToken(getNewTokenRequest);

		 // then
		 assertThat(response.accessToken()).isEqualTo("expired");
	 }

	 @Test
	 void testCreateNewTokenRefreshTokenNotFound() {
		 // given
		 String refreshToken = "Bearer invalidRefreshToken";
		 String uuid = "invalidUuid";

		 GetNewTokenRequest getNewTokenRequest = new GetNewTokenRequest(refreshToken);

		 given(tokenProvider.getUUID(refreshToken.replace("Bearer ", ""))).willReturn(uuid);
		 given(tokenService.getRefreshToken(uuid)).willReturn(null);

		 // when
		 GetNewTokenResponse response = authenticationService.createNewToken(getNewTokenRequest);

		 // then
		 assertThat(response.accessToken()).isEqualTo("expired");
	 }

	 @Test
	 void testIsExpireAccessToken() {
		 // given
		 String accessToken = "validAccessToken";
		 String uuid = "validUuid";
		 IsExpireAccessTokenRequest request = new IsExpireAccessTokenRequest(accessToken);

		 given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		 given(tokenService.existsByUuid(uuid)).willReturn(true);

		 // when
		 IsExpireAccessTokenResponse response = authenticationService.isExpireAccessToken(request);

		 // then
		 assertThat(response.isExpire()).isFalse();
	 }

	 @Test
	 void testIsExpireAccessTokenNotFound() {
		 // given
		 String accessToken = "validAccessToken";
		 String uuid = "validUuid";
		 IsExpireAccessTokenRequest request = new IsExpireAccessTokenRequest(accessToken);

		 given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		 given(tokenService.existsByUuid(uuid)).willReturn(false);

		 // when
		 IsExpireAccessTokenResponse response = authenticationService.isExpireAccessToken(request);

		 // then
		 assertThat(response.isExpire()).isTrue();
	 }

	 @Test
	 void testIsExpireAccessTokenExpiredToken() {
		 // given
		 String accessToken = "expiredAccessToken";
		 IsExpireAccessTokenRequest request = new IsExpireAccessTokenRequest(accessToken);

		 given(tokenProvider.getUUID(accessToken)).willThrow(new ExpiredJwtException(null, null, "Expired token"));

		 // when/then
		 try {
			 authenticationService.isExpireAccessToken(request);
		 } catch (JwtException e) {
			 assertThat(e).isInstanceOf(JwtException.class).hasMessage("Expired token");
		 }
	 }

	 @Test
	 void testIsExpireAccessTokenInvalidToken() {
		 // given
		 String accessToken = "invalidAccessToken";
		 IsExpireAccessTokenRequest request = new IsExpireAccessTokenRequest(accessToken);

		 given(tokenProvider.getUUID(accessToken)).willThrow(new JwtException("Invalid token"));

		 // when/then
		 try {
			 authenticationService.isExpireAccessToken(request);
		 } catch (JwtException e) {
			 assertThat(e).isInstanceOf(JwtException.class).hasMessage("Invalid token");
		 }
	 }

	 @Test
	 void testGetMembersStatusNoResponse() {
		 // given
		 String accessToken = "validAccessToken";
		 String uuid = "validUuid";
		 long membersId = 1L;

		 AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			 UUID.randomUUID().toString(),
			 "refreshUuid",
			 membersId,
			 "ROLE_MEMBERS",
			 LocalDateTime.now().plusHours(1),
			 LocalDateTime.now()
		 );

		 GetMembersStatusRequest request = new GetMembersStatusRequest(accessToken);

		 given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		 given(tokenService.getAccessToken(uuid)).willReturn(accessTokenInfo);
		 given(customMembersDetailsClient.getMemberDormantStatus(any(GetDormantMembersRequest.class)))
			 .willReturn(null);

		 // when
		 GetMembersStatusResponse response = authenticationService.getMembersStatus(request);

		 // then
		 assertThat(response.memberStatusId()).isEqualTo(0);
	 }



	@Test
	void testGetMembersStatusDormant() {
		// given
		String accessToken = "validAccessToken";
		String uuid = "validUuid";
		long membersId = 1L;

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			UUID.randomUUID().toString(),
			"refreshUuid",
			membersId,
			"ROLE_MEMBERS",
			LocalDateTime.now().plusHours(1),
			LocalDateTime.now()
		);

		GetDormantMembersResponse dormantMembersResponse = new GetDormantMembersResponse(2L); // Dormant status

		GetMembersStatusRequest request = new GetMembersStatusRequest(accessToken);

		given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		given(tokenService.getAccessToken(uuid)).willReturn(accessTokenInfo);
		given(customMembersDetailsClient.getMemberDormantStatus(any(GetDormantMembersRequest.class)))
			.willReturn(ApiResponse.success(dormantMembersResponse));

		// when
		GetMembersStatusResponse response = authenticationService.getMembersStatus(request);

		// then
		assertThat(response.memberStatusId()).isEqualTo(2);
		assertThat(response.uuid()).isEqualTo(uuid);

		// Capture the argument passed to saveDormant
		ArgumentCaptor<DormantMembers> dormanMembersCaptor = ArgumentCaptor.forClass(DormantMembers.class);
		verify(tokenService).saveDormant(dormanMembersCaptor.capture());

		// Verify the captured argument
		DormantMembers capturedDormantMembers = dormanMembersCaptor.getValue();
		assertThat(capturedDormantMembers.getUuid()).isEqualTo(uuid);
		assertThat(capturedDormantMembers.getMembersId()).isEqualTo(membersId);
	}
	@Test
	void testGetMembersStatusActive() {
		// given
		String accessToken = "validAccessToken";
		String uuid = "validUuid";
		long membersId = 1L;

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			UUID.randomUUID().toString(),
			"refreshUuid",
			membersId,
			"ROLE_MEMBERS",
			LocalDateTime.now().plusHours(1),
			LocalDateTime.now()
		);

		GetDormantMembersResponse dormantMembersResponse = new GetDormantMembersResponse(1L); // Active status

		GetMembersStatusRequest request = new GetMembersStatusRequest(accessToken);

		given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		given(tokenService.getAccessToken(uuid)).willReturn(accessTokenInfo);
		given(customMembersDetailsClient.getMemberDormantStatus(any(GetDormantMembersRequest.class)))
			.willReturn(ApiResponse.success(dormantMembersResponse));

		// when
		GetMembersStatusResponse response = authenticationService.getMembersStatus(request);

		// then
		assertThat(response.memberStatusId()).isEqualTo(1);
		assertThat(response.uuid()).isNull();

		// Verify no interaction with saveDormant
		verify(tokenService, never()).saveDormant(any(DormantMembers.class));
	}

	@Test
	void testGetMembersStatusDeactivated() {
		// given
		String accessToken = "validAccessToken";
		String uuid = "validUuid";
		long membersId = 1L;

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			UUID.randomUUID().toString(),
			"refreshUuid",
			membersId,
			"ROLE_MEMBERS",
			LocalDateTime.now().plusHours(1),
			LocalDateTime.now()
		);

		GetDormantMembersResponse dormantMembersResponse = new GetDormantMembersResponse(3L); // Deactivated status

		GetMembersStatusRequest request = new GetMembersStatusRequest(accessToken);

		given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		given(tokenService.getAccessToken(uuid)).willReturn(accessTokenInfo);
		given(customMembersDetailsClient.getMemberDormantStatus(any(GetDormantMembersRequest.class)))
			.willReturn(ApiResponse.success(dormantMembersResponse));

		// when
		GetMembersStatusResponse response = authenticationService.getMembersStatus(request);

		// then
		assertThat(response.memberStatusId()).isEqualTo(3);
		assertThat(response.uuid()).isNull();

		// Verify deleteAllTokensByAccessToken was called
		verify(tokenService).deleteAllTokensByAccessToken(uuid);
	}

	@Test
	void testGetMembersStatusNullResponse() {
		// given
		String accessToken = "validAccessToken";
		String uuid = "validUuid";
		long membersId = 1L;

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			UUID.randomUUID().toString(),
			"refreshUuid",
			membersId,
			"ROLE_MEMBERS",
			LocalDateTime.now().plusHours(1),
			LocalDateTime.now()
		);

		GetMembersStatusRequest request = new GetMembersStatusRequest(accessToken);

		given(tokenProvider.getUUID(accessToken)).willReturn(uuid);
		given(tokenService.getAccessToken(uuid)).willReturn(accessTokenInfo);
		given(customMembersDetailsClient.getMemberDormantStatus(any(GetDormantMembersRequest.class)))
			.willReturn(null); // No response body

		// when
		GetMembersStatusResponse response = authenticationService.getMembersStatus(request);

		// then
		assertThat(response.memberStatusId()).isEqualTo(0);
		assertThat(response.uuid()).isNull();

		// Verify no interaction with saveDormant and deleteAllTokensByAccessToken
		verify(tokenService, never()).saveDormant(any(DormantMembers.class));
		verify(tokenService, never()).deleteAllTokensByAccessToken(any(String.class));
	}
}
