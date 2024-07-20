package store.novabook.auth.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.util.KeyManagerUtil;
import store.novabook.auth.util.dto.JWTConfigDto;

import org.mockito.MockedStatic;

class TokenProviderTest {

	@Mock
	private Environment environment;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private TokenProvider tokenProvider;

	private JWTConfigDto jwtConfigDto;

	private Key key;

	private MockedStatic<KeyManagerUtil> keyManagerUtilMockedStatic;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		jwtConfigDto = new JWTConfigDto("header",
			"JDJhJDEyJHk0RC5jVkM1UzcwMExHUlU4VXdFZmVPTkZ6cWlZclJOamdTMnVxV1F1VXEyRnYybXpFTFhX",
			86400);
		byte[] keyBytes = Decoders.BASE64.decode(jwtConfigDto.secret());
		key = Keys.hmacShaKeyFor(keyBytes);

		keyManagerUtilMockedStatic = mockStatic(KeyManagerUtil.class);
		keyManagerUtilMockedStatic.when(() -> KeyManagerUtil.getJWTConfig(any(Environment.class), any(RestTemplate.class)))
			.thenReturn(jwtConfigDto);

		tokenProvider.afterPropertiesSet();
	}

	@AfterEach
	void tearDown() {
		if (keyManagerUtilMockedStatic != null) {
			keyManagerUtilMockedStatic.close();
		}
	}

	@Test
	void createAccessToken() {
		UUID uuid = UUID.randomUUID();
		String token = tokenProvider.createAccessToken(uuid);

		assertNotNull(token);
		String tokenUuid = tokenProvider.getUUID(token);
		assertEquals(uuid.toString(), tokenUuid);
	}

	@Test
	void createAccessTokenFromRefreshToken() {

		String uuid = "123e4567-e89b-12d3-a456-426614174000";
		String refreshTokenUUID = "123e4567-e89b-12d3-a456-426614174001";
		long membersId = 1L;
		String role = "ROLE_MEMBERS";
		LocalDateTime expirationTime = LocalDateTime.now().plusDays(1);

		AccessTokenInfo accessTokenInfo = AccessTokenInfo.of(uuid, refreshTokenUUID, membersId, role, expirationTime);


		String token = tokenProvider.createAccessTokenFromRefreshToken(accessTokenInfo);

		assertNotNull(token);
		String tokenUuid = tokenProvider.getUUID(token);
		assertEquals(accessTokenInfo.getUuid(), tokenUuid);
	}

	@Test
	void createRefreshToken() {
		UUID uuid = UUID.randomUUID();
		String token = tokenProvider.createRefreshToken(uuid);

		assertNotNull(token);
		String tokenUuid = tokenProvider.getUUID(token);
		assertEquals(uuid.toString(), tokenUuid);
	}

	@Test
	void getUUID_ValidToken() {
		UUID uuid = UUID.randomUUID();
		String token = tokenProvider.createAccessToken(uuid);

		String tokenUuid = tokenProvider.getUUID(token);

		assertEquals(uuid.toString(), tokenUuid);
	}

	@Test
	void getUUID_InvalidToken() {
		assertThrows(JwtException.class, () -> tokenProvider.getUUID("invalid-token"));
	}

	@Test
	void validateToken_Valid() {
		UUID uuid = UUID.randomUUID();
		String token = tokenProvider.createAccessToken(uuid);

		assertTrue(tokenProvider.validateToken(token));
	}

	@Test
	void validateToken_Invalid() {
		assertFalse(tokenProvider.validateToken("invalid-token"));
	}
}
