package store.novabook.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.DormantMembers;
import store.novabook.auth.entity.RefreshTokenInfo;

class TokenServiceTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@InjectMocks
	private TokenService tokenService;

	@Mock
	private CustomUserDetails principal;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	void testSaveTokens() {
		// given
		String accessTokenUuid = UUID.randomUUID().toString();
		String refreshTokenUuid = UUID.randomUUID().toString();

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			accessTokenUuid, refreshTokenUuid, 1L, "ROLE_USER",
			LocalDateTime.now().plusMinutes(5), LocalDateTime.now()
		);
		RefreshTokenInfo refreshTokenInfo = new RefreshTokenInfo(
			refreshTokenUuid, accessTokenUuid, 1L, "ROLE_USER",
			LocalDateTime.now().plusDays(30), LocalDateTime.now()
		);


		// when
		tokenService.saveTokens(accessTokenInfo, refreshTokenInfo);

		when(tokenService.getAccessToken(accessTokenUuid)).thenReturn(accessTokenInfo);
		when(tokenService.getRefreshToken(refreshTokenUuid)).thenReturn(refreshTokenInfo);

		AccessTokenInfo accessToken = tokenService.getAccessToken(accessTokenUuid);
		RefreshTokenInfo refreshToken = tokenService.getRefreshToken(refreshTokenUuid);

		assertThat(accessToken).isEqualTo(accessTokenInfo);
		assertThat(refreshToken).isEqualTo(refreshTokenInfo);
	}

	@Test
	void testGetAccessToken() throws Exception {
		// given
		String uuid = UUID.randomUUID().toString();
		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			uuid, "refreshUuid", 1L, "ROLE_USER",
			LocalDateTime.now().plusMinutes(5), LocalDateTime.now()
		);

		when(redisTemplate.opsForValue().get(uuid)).thenReturn(accessTokenInfo);
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		when(objectMapper.readValue("{}", AccessTokenInfo.class)).thenReturn(accessTokenInfo);

		// when
		AccessTokenInfo actualAccessTokenInfo = tokenService.getAccessToken(uuid);

		// then
		assertThat(actualAccessTokenInfo).isEqualTo(accessTokenInfo);
	}


	@Test
	void changeAccessTokenTest() {
		// Given
		String accessTokenUuid = "access-token-uuid";
		String refreshTokenUuid = "refresh-token-uuid";
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime accessTokenExpirationTime = now.plusMinutes(10);
		LocalDateTime refreshTokenExpirationTime = now.plusDays(1);
		AccessTokenInfo accessTokenInfo = AccessTokenInfo.of(accessTokenUuid, refreshTokenUuid, 1L, "ROLE_USER", accessTokenExpirationTime);
		RefreshTokenInfo refreshTokenInfo = RefreshTokenInfo.of(refreshTokenUuid, accessTokenUuid, 1L, "ROLE_USER", refreshTokenExpirationTime);

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(tokenService.getRefreshToken(refreshTokenUuid)).thenReturn(refreshTokenInfo);

		// When
		tokenService.changeAccessToken(refreshTokenInfo, accessTokenInfo);

		// Then
		verify(redisTemplate, times(1)).delete(accessTokenUuid);
		verify(redisTemplate, times(4)).opsForValue(); // Called twice, once for each token
		verify(valueOperations, times(1)).set(eq(accessTokenUuid), eq(accessTokenInfo), any(Duration.class));
		verify(valueOperations, times(1)).set(eq(refreshTokenUuid), any(RefreshTokenInfo.class), any(Duration.class));
	}


	@Test
	void testDeleteAllTokensByAccessToken() throws Exception {
		// given
		String accessTokenUUID = UUID.randomUUID().toString();
		String refreshTokenUUID = UUID.randomUUID().toString();

		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			accessTokenUUID, refreshTokenUUID, 1L, "ROLE_USER",
			LocalDateTime.now().plusMinutes(5), LocalDateTime.now()
		);

		when(redisTemplate.opsForValue().get(accessTokenUUID)).thenReturn(accessTokenInfo);
		when(objectMapper.writeValueAsString(accessTokenInfo)).thenReturn("{}");
		when(objectMapper.readValue("{}", AccessTokenInfo.class)).thenReturn(accessTokenInfo);

		// when
		tokenService.deleteAllTokensByAccessToken(accessTokenUUID);

		// then
		verify(redisTemplate).delete(accessTokenUUID);
		verify(redisTemplate).delete(refreshTokenUUID);
	}

	@Test
	void existsByUuid_ReturnsTrue() {
		// Given
		String uuid = "existing-uuid";
		when(redisTemplate.hasKey(uuid)).thenReturn(true);

		// When
		boolean exists = tokenService.existsByUuid(uuid);

		// Then
		assertTrue(exists);
	}

	@Test
	void existsByUuid_ReturnsFalse() {
		// Given
		String uuid = "non-existing-uuid";
		when(redisTemplate.hasKey(uuid)).thenReturn(false);

		// When
		boolean exists = tokenService.existsByUuid(uuid);

		// Then
		assertFalse(exists);
	}


	@Test
	void testSaveDormantWithExistingKey() {
		// given
		String uuid = UUID.randomUUID().toString();
		DormantMembers dormantMembers = DormantMembers.of(uuid, 1L);
		when(redisTemplate.hasKey(uuid)).thenReturn(true);

		// when
		tokenService.saveDormant(dormantMembers);

		verify(redisTemplate).opsForValue();
	}

	@Test
	void testSaveDormantWithNonExistingKey() {
		// given
		String uuid = UUID.randomUUID().toString();
		DormantMembers dormantMembers = DormantMembers.of(uuid, 1L);
		when(redisTemplate.hasKey(uuid)).thenReturn(false);

		// when
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			tokenService.saveDormant(dormantMembers);
		});

		// then
		assertThat(exception.getMessage()).isEqualTo("No auth found with uuid: " + uuid);
		verify(redisTemplate).hasKey(uuid);
	}

	@Test
	void testGetDormantSuccess() throws Exception {
		// given
		String uuid = UUID.randomUUID().toString();
		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			uuid, "refreshUuid", 1L, "ROLE_USER",
			LocalDateTime.now().plusMinutes(5), LocalDateTime.now()
		);
		String jsonString = "{}";
		when(valueOperations.get(uuid)).thenReturn(accessTokenInfo);
		when(objectMapper.writeValueAsString(accessTokenInfo)).thenReturn(jsonString);
		when(objectMapper.readValue(jsonString, AccessTokenInfo.class)).thenReturn(accessTokenInfo);

		// when
		DormantMembers dormantMembers = tokenService.getDormant(uuid);

		// then
		assertThat(dormantMembers).isNotNull();
		assertThat(dormantMembers.getUuid()).isEqualTo(uuid);
		assertThat(dormantMembers.getMembersId()).isEqualTo(1L);
	}

	@Test
	void testGetDormantNotFound() {
		// given
		String uuid = UUID.randomUUID().toString();
		when(valueOperations.get(uuid)).thenReturn(null);

		// when
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			tokenService.getDormant(uuid);
		});

		// then
		assertThat(exception.getMessage()).contains("Failed to deserialize access token with uuid: " + uuid);
	}

	@Test
	void testGetDormantDeserializationFailure() throws Exception {
		// given
		String uuid = UUID.randomUUID().toString();
		AccessTokenInfo accessTokenInfo = new AccessTokenInfo(
			uuid, "refreshUuid", 1L, "ROLE_USER",
			LocalDateTime.now().plusMinutes(5), LocalDateTime.now()
		);
		String jsonString = "{}";
		when(valueOperations.get(uuid)).thenReturn(accessTokenInfo);
		when(objectMapper.writeValueAsString(accessTokenInfo)).thenReturn(jsonString);
		when(objectMapper.readValue(jsonString, AccessTokenInfo.class)).thenThrow(new RuntimeException("Deserialization error"));

		// when
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			tokenService.getDormant(uuid);
		});

		// then
		assertThat(exception.getMessage()).contains("Failed to deserialize access token with uuid: " + uuid);
		assertThat(exception.getCause().getMessage()).contains("Deserialization error");
	}


	@Test
	void testCreateRefreshTokenInfo() {
		// given
		long membersId = 1L;
		String role = "ROLE_USER";
		when(principal.getMembersId()).thenReturn(membersId);
		when(principal.getRole()).thenReturn(role);

		// when
		RefreshTokenInfo refreshTokenInfo = tokenService.createRefreshTokenInfo(principal);

		// then
		assertThat(refreshTokenInfo).isNotNull();
		assertThat(refreshTokenInfo.getUuid()).isNotNull();
		assertThat(refreshTokenInfo.getMembersId()).isEqualTo(membersId);
		assertThat(refreshTokenInfo.getRole()).isEqualTo(role);
		assertThat(refreshTokenInfo.getExpirationTime()).isAfter(LocalDateTime.now());
	}

	@Test
	void testCreateAccessTokenInfo() {
		// given
		long membersId = 1L;
		String role = "ROLE_USER";
		RefreshTokenInfo refreshTokenInfo = RefreshTokenInfo.of(
			UUID.randomUUID().toString(), null, membersId, role, LocalDateTime.now().plusDays(30));

		when(principal.getMembersId()).thenReturn(membersId);
		when(principal.getRole()).thenReturn(role);

		// when
		AccessTokenInfo accessTokenInfo = tokenService.createAccessTokenInfo(principal, refreshTokenInfo);

		// then
		assertThat(accessTokenInfo).isNotNull();
		assertThat(accessTokenInfo.getUuid()).isNotNull();
		assertThat(accessTokenInfo.getMembersId()).isEqualTo(membersId);
		assertThat(accessTokenInfo.getRole()).isEqualTo(role);
		assertThat(accessTokenInfo.getExpirationTime()).isAfter(LocalDateTime.now());
		assertThat(accessTokenInfo.getRefreshTokenUUID()).isEqualTo(refreshTokenInfo.getUuid());
	}


	@Test
	void testCreateAccessTokenInfo2() {
		// given
		long membersId = 1L;
		String role = "ROLE_USER";
		RefreshTokenInfo refreshTokenInfo = RefreshTokenInfo.of(
			UUID.randomUUID().toString(), null, membersId, role, LocalDateTime.now().plusDays(30));

		// when
		AccessTokenInfo accessTokenInfo = tokenService.createAccessTokenInfo(refreshTokenInfo);

		// then
		assertThat(accessTokenInfo).isNotNull();
		assertThat(accessTokenInfo.getUuid()).isNotNull();
		assertThat(accessTokenInfo.getMembersId()).isEqualTo(membersId);
		assertThat(accessTokenInfo.getRole()).isEqualTo(role);
		assertThat(accessTokenInfo.getExpirationTime()).isAfter(LocalDateTime.now());
		assertThat(accessTokenInfo.getRefreshTokenUUID()).isEqualTo(refreshTokenInfo.getUuid());
	}

	@Test
	void testCreatePaycoRefreshTokenInfo2() {
		// given
		long membersId = 1L;

		// when
		RefreshTokenInfo refreshTokenInfo = tokenService.createPaycoRefreshTokenInfo(membersId);

		// then
		assertThat(refreshTokenInfo).isNotNull();
		assertThat(refreshTokenInfo.getUuid()).isNotNull();
		assertThat(refreshTokenInfo.getMembersId()).isEqualTo(membersId);
		assertThat(refreshTokenInfo.getRole()).isEqualTo("ROLE_MEMBERS");
		assertThat(refreshTokenInfo.getExpirationTime()).isAfter(LocalDateTime.now());
	}

	@Test
	void testCreatePaycoAccessTokenInfo() {
		// given
		long membersId = 1L;
		String refreshTokenUUID = UUID.randomUUID().toString();
		RefreshTokenInfo refreshTokenInfo = RefreshTokenInfo.of(
			refreshTokenUUID, null, membersId, "ROLE_MEMBERS", LocalDateTime.now().plusDays(30));

		// when
		AccessTokenInfo accessTokenInfo = tokenService.createPaycoAccessTokenInfo(membersId, refreshTokenInfo);

		// then
		assertThat(accessTokenInfo).isNotNull();
		assertThat(accessTokenInfo.getUuid()).isNotNull();
		assertThat(accessTokenInfo.getMembersId()).isEqualTo(membersId);
		assertThat(accessTokenInfo.getRole()).isEqualTo("ROLE_MEMBERS");
		assertThat(accessTokenInfo.getExpirationTime()).isAfter(LocalDateTime.now());
		assertThat(accessTokenInfo.getRefreshTokenUUID()).isEqualTo(refreshTokenInfo.getUuid());
	}
}
