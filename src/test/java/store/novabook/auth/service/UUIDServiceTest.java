package store.novabook.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import store.novabook.auth.dto.request.GetMembersUUIDRequest;
import store.novabook.auth.dto.response.GetDormantMembersUUIDResponse;
import store.novabook.auth.dto.response.GetMembersTokenResponse;
import store.novabook.auth.dto.response.GetMembersUUIDResponse;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.DormantMembers;
import store.novabook.auth.jwt.TokenProvider;

class UUIDServiceTest {

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private TokenService tokenService;

	@InjectMocks
	private UUIDService uuidService; // Replace SomeService with the actual class name

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetMembersUUID() {
		// given
		String uuid = UUID.randomUUID().toString();
		GetMembersUUIDRequest request = new GetMembersUUIDRequest(uuid);

		AccessTokenInfo accessTokenInfo = AccessTokenInfo.of(
			uuid, "refreshTokenUUID", 1L, "ROLE_USER",
			LocalDateTime.now().plusMinutes(5));

		when(tokenService.getAccessToken(uuid)).thenReturn(accessTokenInfo);

		// when
		GetMembersUUIDResponse response = uuidService.getMembersUUID(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.membersId()).isEqualTo(1L);
		assertThat(response.role()).isEqualTo("ROLE_USER");

		verify(tokenService).getAccessToken(uuid);
	}

	@Test
	void testGetDormantMembersId() {
		// given
		String uuid = UUID.randomUUID().toString();
		GetMembersUUIDRequest request = new GetMembersUUIDRequest(uuid);

		DormantMembers dormantMembers = DormantMembers.of(uuid, 1L);

		when(tokenService.getDormant(uuid)).thenReturn(dormantMembers);

		// when
		GetDormantMembersUUIDResponse response = uuidService.getDormantMembersId(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.membersId()).isEqualTo(1L);

		verify(tokenService).getDormant(uuid);
	}

	@Test
	void testGetMembersTokenWithValidAccessToken() {
		// given
		String accessToken = UUID.randomUUID().toString();
		String refreshToken = UUID.randomUUID().toString();
		String uuid = UUID.randomUUID().toString();
		long membersId = 123L;

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			uuid, refreshToken, membersId, "ROLE_USER",
			LocalDateTime.now().plusMinutes(5), LocalDateTime.now()
		);

		when(tokenProvider.validateToken(accessToken)).thenReturn(true);
		when(tokenProvider.getUUID(accessToken)).thenReturn(uuid);
		when(tokenService.getAccessToken(uuid)).thenReturn(accessTokenInfo);

		// when
		GetMembersTokenResponse response = uuidService.getMembersToken(accessToken, refreshToken);

		// then
		assertThat(response).isNotNull();
		assertThat(response.membersId()).isEqualTo(membersId);

		verify(tokenProvider).validateToken(accessToken);
		verify(tokenProvider).getUUID(accessToken);
		verify(tokenService).getAccessToken(uuid);
	}

	@Test
	void testGetMembersTokenWithInvalidAccessToken() {
		// given
		String accessToken = UUID.randomUUID().toString();
		String refreshToken = UUID.randomUUID().toString();
		String uuid = UUID.randomUUID().toString();
		long membersId = 123L;

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			uuid, refreshToken, membersId, "ROLE_USER",
			LocalDateTime.now().plusMinutes(5), LocalDateTime.now()
		);

		when(tokenProvider.validateToken(accessToken)).thenReturn(false);
		when(tokenProvider.getUUID(refreshToken)).thenReturn(uuid);
		when(tokenService.getAccessToken(uuid)).thenReturn(accessTokenInfo);

		// when
		GetMembersTokenResponse response = uuidService.getMembersToken(accessToken, refreshToken);

		// then
		assertThat(response).isNotNull();
		assertThat(response.membersId()).isEqualTo(membersId);

		verify(tokenProvider).validateToken(accessToken);
		verify(tokenProvider).getUUID(refreshToken);
		verify(tokenService).getAccessToken(uuid);
	}
}