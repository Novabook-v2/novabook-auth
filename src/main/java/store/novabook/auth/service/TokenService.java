package store.novabook.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.RefreshTokenInfo;

@Slf4j
@Service
public class TokenService {

	private final RedisTemplate<String, Object> redisTemplate;

	private static final String ACCESS_TOKEN_KEY_PREFIX = "accessToken:";
	private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";

	public TokenService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// Save AccessTokenInfo and RefreshTokenInfo together
	public void saveTokens(AccessTokenInfo accessTokenInfo, RefreshTokenInfo refreshTokenInfo) {
		// Save AccessTokenInfo and RefreshTokenInfo using composite keys
		redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY_PREFIX + accessTokenInfo.getUuid(), accessTokenInfo);
		redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY_PREFIX + refreshTokenInfo.getUuid(), refreshTokenInfo);
	}

	// Retrieve AccessTokenInfo by UUID
	public AccessTokenInfo getAccessTokenInfoByUUID(String accessTokenUUID) {
		try {
			return (AccessTokenInfo) redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY_PREFIX + accessTokenUUID);
		} catch (Exception e) {
			log.error("Error while getting AccessTokenInfo from Redis", e);
			throw new RuntimeException(e);
		}
	}

	// Retrieve RefreshTokenInfo by UUID
	public RefreshTokenInfo getRefreshTokenInfoByUUID(String refreshTokenUUID) {
		try {
			return (RefreshTokenInfo) redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + refreshTokenUUID);
		} catch (Exception e) {
			log.error("Error while getting RefreshTokenInfo from Redis", e);
			throw new RuntimeException(e);
		}
	}
}
