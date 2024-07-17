package store.novabook.auth.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.DormantMembers;
import store.novabook.auth.entity.RefreshTokenInfo;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;


	public void saveTokens(AccessTokenInfo accessTokenInfo, RefreshTokenInfo refreshTokenInfo) {
		LocalDateTime now = LocalDateTime.now();
		Duration accessTokenDuration = Duration.between(now, accessTokenInfo.getExpirationTime());
		Duration refreshTokenDuration = Duration.between(now, refreshTokenInfo.getExpirationTime());
		redisTemplate.opsForValue().set(accessTokenInfo.getUuid(), accessTokenInfo, accessTokenDuration);
		redisTemplate.opsForValue().set(refreshTokenInfo.getUuid(), refreshTokenInfo, refreshTokenDuration);
	}

	public AccessTokenInfo getAccessToken(String uuid) {
		Object object = redisTemplate.opsForValue().get(uuid);
		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(object);
			return objectMapper.readValue(jsonString, AccessTokenInfo.class);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException();
		}

	}

	public RefreshTokenInfo getRefreshToken(String uuid) {
		Object object = redisTemplate.opsForValue().get(uuid);
		try {
			String jsonString = objectMapper.writeValueAsString(object);
			return objectMapper.readValue(jsonString, RefreshTokenInfo.class);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Failed to deserialize refresh token with uuid: " + uuid, e);
		}

	}

	public void changeAccessToken(RefreshTokenInfo refreshTokenInfo, AccessTokenInfo accessTokenInfo) {

		LocalDateTime now = LocalDateTime.now();
		Duration accessTokenDuration = Duration.between(now, accessTokenInfo.getExpirationTime());
		Duration refreshTokenDuration = Duration.between(now, refreshTokenInfo.getExpirationTime());

		redisTemplate.delete(accessTokenInfo.getUuid());
		redisTemplate.opsForValue().set(accessTokenInfo.getUuid(), accessTokenInfo, accessTokenDuration);

		RefreshTokenInfo refreshToken = getRefreshToken(refreshTokenInfo.getUuid());
		refreshToken.setAccessTokenUUID(accessTokenInfo.getUuid());

		redisTemplate.opsForValue().set(refreshToken.getUuid(), refreshToken, refreshTokenDuration);
	}


	public void deleteAllTokensByAccessToken(String accessTokenUUID) {
		AccessTokenInfo accessTokenInfo = getAccessToken(accessTokenUUID);

		if (accessTokenInfo != null) {
			redisTemplate.delete(accessTokenUUID);
			redisTemplate.delete(accessTokenInfo.getRefreshTokenUUID());
		}
	}

	public boolean existsByUuid(String uuid) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(uuid));
	}


	public void saveDormant(DormantMembers dormantMembers) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(dormantMembers.getUuid()))) {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(300);
			Duration duration = Duration.between(now, expirationTime);
			redisTemplate.opsForValue().set(dormantMembers.getUuid(), dormantMembers, duration);
		} else {
			throw new IllegalArgumentException("No auth found with uuid: " + dormantMembers.getUuid());
		}

	}

	public DormantMembers getDormant(String uuid) {

		Object object = redisTemplate.opsForValue().get(uuid);
		try {
			String jsonString = objectMapper.writeValueAsString(object);
			AccessTokenInfo accessTokenInfo = objectMapper.readValue(jsonString, AccessTokenInfo.class);
			return DormantMembers.of(accessTokenInfo.getUuid(),
				accessTokenInfo.getMembersId());
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to deserialize access token with uuid: " + uuid, e);
		}
	}

	public RefreshTokenInfo createRefreshTokenInfo(CustomUserDetails principal) {
		Date now = new Date();
		Date refreshValidity = new Date(now.getTime() + 12000 * 1000);

		String refreshTokenUUID = UUID.randomUUID().toString();

		return RefreshTokenInfo.of(refreshTokenUUID, null,
			principal.getMembersId(), principal.getRole(),
			LocalDateTime.ofInstant(refreshValidity.toInstant(), ZoneId.systemDefault()));
	}

	public AccessTokenInfo createAccessTokenInfo(CustomUserDetails principal, RefreshTokenInfo refreshTokenInfo) {
		Date now = new Date();
		Date accessValidity = new Date(now.getTime() + 300 * 1000);

		String accessTokenUUID = UUID.randomUUID().toString();

		return AccessTokenInfo.of(accessTokenUUID, refreshTokenInfo.getUuid(),
			principal.getMembersId(), principal.getRole(),
			LocalDateTime.ofInstant(accessValidity.toInstant(), ZoneId.systemDefault()));
	}

	public AccessTokenInfo createAccessTokenInfo(RefreshTokenInfo refreshTokenInfo) {
		Date now = new Date();
		Date accessValidity = new Date(now.getTime() + 300 * 1000);

		String accessTokenUUID = UUID.randomUUID().toString();

		return AccessTokenInfo.of(accessTokenUUID, refreshTokenInfo.getUuid(),
			refreshTokenInfo.getMembersId(), refreshTokenInfo.getRole(),
			LocalDateTime.ofInstant(accessValidity.toInstant(), ZoneId.systemDefault()));
	}

	public RefreshTokenInfo createPaycoRefreshTokenInfo(long membersId) {
		Date now = new Date();
		Date refreshValidity = new Date(now.getTime() + 12000 * 1000);

		String refreshTokenUUID = UUID.randomUUID().toString();

		return RefreshTokenInfo.of(refreshTokenUUID, null,
			membersId, "ROLE_MEMBERS",
			LocalDateTime.ofInstant(refreshValidity.toInstant(), ZoneId.systemDefault()));
	}

	public AccessTokenInfo createPaycoAccessTokenInfo(long membersId, RefreshTokenInfo refreshTokenInfo) {
		Date now = new Date();
		Date accessValidity = new Date(now.getTime() + 300 * 1000);

		String accessTokenUUID = UUID.randomUUID().toString();

		return AccessTokenInfo.of(accessTokenUUID, refreshTokenInfo.getUuid(),
			membersId, "ROLE_MEMBERS",
			LocalDateTime.ofInstant(accessValidity.toInstant(), ZoneId.systemDefault()));
	}
}