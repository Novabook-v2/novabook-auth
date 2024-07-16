package store.novabook.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.JWTTokenInfo;
import store.novabook.auth.entity.MembersInfo;
import store.novabook.auth.entity.RefreshTokenInfo;

@Slf4j
@Service
public class JWTTokenService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	private static final String JWT_TOKEN_KEY_PREFIX = "jwtToken:";
	private static final String ACCESS_TOKEN_KEY_PREFIX = "accessToken:";
	private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";
	private static final String ACCESS_TOKEN_INDEX = "accessTokenIndex:";
	private static final String REFRESH_TOKEN_INDEX = "refreshTokenIndex:";

	public JWTTokenService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	// Save JWTTokenInfo in Redis with composite keys
	public void saveJWTTokenInfo(String jwtTokenKey, JWTTokenInfo jwtTokenInfo) throws JsonProcessingException {
		// Serialize JWTTokenInfo components
		Map<String, String> jwtTokenInfoMap = serializeJWTTokenInfo(jwtTokenInfo);

		// Save JWTTokenInfo using composite key
		redisTemplate.opsForHash().putAll(JWT_TOKEN_KEY_PREFIX + jwtTokenKey, jwtTokenInfoMap);

		// Save accessTokenInfo and refreshTokenInfo UUIDs with corresponding composite keys
		redisTemplate.opsForValue()
			.set(ACCESS_TOKEN_KEY_PREFIX + jwtTokenInfo.getAccessTokenInfo().getUuid(), jwtTokenKey);
		redisTemplate.opsForValue()
			.set(REFRESH_TOKEN_KEY_PREFIX + jwtTokenInfo.getRefreshTokenInfo().getUuid(), jwtTokenKey);
	}

	// Retrieve JWTTokenInfo by UUID
	public JWTTokenInfo getJWTTokenInfoByAccessTokenUUID(String accessTokenUUID) {
		try {
			String jwtTokenKey = (String)redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY_PREFIX + accessTokenUUID);
			if (jwtTokenKey != null) {
				return getJWTTokenInfo(jwtTokenKey);
			}
			return null;
		} catch (Exception e) {
			log.error("Error while getting JWTTokenInfo by AccessTokenUUID from Redis", e);
			throw new RuntimeException(e);
		}
	}

	public JWTTokenInfo getJWTTokenInfoByRefreshTokenUUID(String refreshTokenUUID) {
		try {
			String jwtTokenKey = (String)redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + refreshTokenUUID);
			if (jwtTokenKey != null) {
				return getJWTTokenInfo(jwtTokenKey);
			}
			return null;
		} catch (Exception e) {
			log.error("Error while getting JWTTokenInfo by RefreshTokenUUID from Redis", e);
			throw new RuntimeException(e);
		}
	}

	// Retrieve JWTTokenInfo from Redis using composite key
	private JWTTokenInfo getJWTTokenInfo(String jwtTokenKey) {
		try {
			Map<Object, Object> entries = redisTemplate.opsForHash().entries(JWT_TOKEN_KEY_PREFIX + jwtTokenKey);
			String membersInfoJson = (String)entries.get("membersInfo");
			String accessTokenInfoJson = (String)entries.get("accessTokenInfo");
			String refreshTokenInfoJson = (String)entries.get("refreshTokenInfo");

			MembersInfo membersInfo = objectMapper.readValue(membersInfoJson, MembersInfo.class);
			AccessTokenInfo accessTokenInfo = objectMapper.readValue(accessTokenInfoJson, AccessTokenInfo.class);
			RefreshTokenInfo refreshTokenInfo = objectMapper.readValue(refreshTokenInfoJson, RefreshTokenInfo.class);

			return new JWTTokenInfo(membersInfo, accessTokenInfo, refreshTokenInfo);
		} catch (JsonProcessingException e) {
			log.error("Error while getting JWTTokenInfo from Redis", e);
			throw new RuntimeException(e);
		}
	}

	// Utility method to serialize JWTTokenInfo into a map
	private Map<String, String> serializeJWTTokenInfo(JWTTokenInfo jwtTokenInfo) throws JsonProcessingException {
		Map<String, String> map = new HashMap<>();
		map.put("membersInfo", objectMapper.writeValueAsString(jwtTokenInfo.getMembersInfo()));
		map.put("accessTokenInfo", objectMapper.writeValueAsString(jwtTokenInfo.getAccessTokenInfo()));
		map.put("refreshTokenInfo", objectMapper.writeValueAsString(jwtTokenInfo.getRefreshTokenInfo()));
		return map;
	}

	// Delete JWTTokenInfo by AccessToken UUID
	public void deleteJWTTokenInfoByAccessTokenUUID(String accessTokenUUID) {
		try {
			String jwtTokenKey = (String)redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY_PREFIX + accessTokenUUID);
			if (jwtTokenKey != null) {
				deleteJWTTokenInfo(jwtTokenKey);
			}
		} catch (Exception e) {
			log.error("Error while deleting JWTTokenInfo by AccessTokenUUID from Redis", e);
			throw new RuntimeException(e);
		}
	}

	// Delete JWTTokenInfo by RefreshToken UUID
	public void deleteJWTTokenInfoByRefreshTokenUUID(String refreshTokenUUID) {
		try {
			String jwtTokenKey = (String)redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + refreshTokenUUID);
			if (jwtTokenKey != null) {
				deleteJWTTokenInfo(jwtTokenKey);
			}
		} catch (Exception e) {
			log.error("Error while deleting JWTTokenInfo by RefreshTokenUUID from Redis", e);
			throw new RuntimeException(e);
		}
	}

	// Delete JWTTokenInfo from Redis using composite key
	public void deleteJWTTokenInfo(String jwtTokenKey) {
		try {
			JWTTokenInfo jwtTokenInfo = getJWTTokenInfo(jwtTokenKey);
			if (jwtTokenInfo != null) {
				// Delete the JWTTokenInfo composite key
				redisTemplate.delete(JWT_TOKEN_KEY_PREFIX + jwtTokenKey);

				// Delete the accessTokenInfo and refreshTokenInfo UUIDs with corresponding composite keys
				redisTemplate.delete(ACCESS_TOKEN_KEY_PREFIX + jwtTokenInfo.getAccessTokenInfo().getUuid());
				redisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + jwtTokenInfo.getRefreshTokenInfo().getUuid());
			}
		} catch (Exception e) {
			log.error("Error while deleting JWTTokenInfo from Redis", e);
			throw new RuntimeException(e);
		}
	}

	// Check if AccessToken UUID exists
	public boolean existsByAccessTokenUUID(String accessTokenUUID) {
		try {
			return redisTemplate.hasKey(ACCESS_TOKEN_KEY_PREFIX + accessTokenUUID);
		} catch (Exception e) {
			log.error("Error while checking existence of AccessTokenUUID in Redis", e);
			throw new RuntimeException(e);
		}
	}

	// Check if RefreshToken UUID exists
	public boolean existsByRefreshTokenUUID(String refreshTokenUUID) {
		try {
			return redisTemplate.hasKey(REFRESH_TOKEN_KEY_PREFIX + refreshTokenUUID);
		} catch (Exception e) {
			log.error("Error while checking existence of RefreshTokenUUID in Redis", e);
			throw new RuntimeException(e);
		}
	}

	// Check if JWTTokenInfo exists by JWT token key
	public boolean existsByJWTTokenKey(String jwtTokenKey) {
		try {
			return redisTemplate.hasKey(JWT_TOKEN_KEY_PREFIX + jwtTokenKey);
		} catch (Exception e) {
			log.error("Error while checking existence of JWTTokenKey in Redis", e);
			throw new RuntimeException(e);
		}
	}
}