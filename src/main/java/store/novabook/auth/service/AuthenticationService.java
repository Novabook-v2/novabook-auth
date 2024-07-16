package store.novabook.auth.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.AuthenticationInfo;
import store.novabook.auth.entity.DormantMembers;
import store.novabook.auth.entity.RefreshTokenInfo;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

	private final RedisTemplate<String, Object> redisTemplate;

	public void saveAuth(AuthenticationInfo authenticationInfo) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(authenticationInfo.getUuid()))) {
			throw new IllegalArgumentException("Auth already exists for this uuid: " + authenticationInfo.getUuid());
		}
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(now, authenticationInfo.getExpirationTime());
		redisTemplate.opsForValue().set(authenticationInfo.getUuid(), authenticationInfo, duration);
	}

	public void saveTokens(AccessTokenInfo accessTokenInfo, RefreshTokenInfo refreshTokenInfo) {
		// if (Boolean.TRUE.equals(redisTemplate.hasKey(authenticationInfo.getUuid()))) {
		// 	throw new IllegalArgumentException("Auth already exists for this uuid: " + authenticationInfo.getUuid());
		// }
		// LocalDateTime now = LocalDateTime.now();
		// Duration duration = Duration.between(now, authenticationInfo.getExpirationTime());
		// redisTemplate.opsForValue().set(authenticationInfo.getUuid(), authenticationInfo, duration);
		redisTemplate.opsForValue().set(accessTokenInfo.getUuid(), accessTokenInfo);
		redisTemplate.opsForValue().set(refreshTokenInfo.getUuid(), refreshTokenInfo);
	}

	public AccessTokenInfo getAccessToken(String uuid) {
		Object object = redisTemplate.opsForValue().get(uuid);
		if (object instanceof AccessTokenInfo accessTokenInfo) {
			return accessTokenInfo;
		} else {
			throw new IllegalArgumentException("No access token found with uuid: " + uuid);
		}
	}

	public RefreshTokenInfo getRefreshToken(String uuid) {
		Object object = redisTemplate.opsForValue().get(uuid);
		if (object instanceof RefreshTokenInfo refreshTokenInfo) {
			return refreshTokenInfo;
		} else {
			throw new IllegalArgumentException("No refresh token found with uuid: " + uuid);
		}
	}

	public void deleteAccessToken(String accessTokenUUID) {
		AccessTokenInfo accessTokenInfo = (AccessTokenInfo)redisTemplate.opsForValue().get(accessTokenUUID);

		if (accessTokenInfo != null) {
			redisTemplate.delete(accessTokenUUID);
			redisTemplate.delete(accessTokenInfo.getRefreshTokenUUID());
		}
	}

	public void deleteRefreshToken(String refreshTokenUUID) {
		RefreshTokenInfo refreshTokenInfo = (RefreshTokenInfo)redisTemplate.opsForValue().get(refreshTokenUUID);

		if (refreshTokenInfo != null) {
			redisTemplate.delete(refreshTokenUUID);
			redisTemplate.delete(refreshTokenInfo.getAccessTokenUUID());
		}
	}

	public AuthenticationInfo getAuth(String uuid) {
		Object object = redisTemplate.opsForValue().get(uuid);
		if (object instanceof AuthenticationInfo authenticationInfo) {
			return authenticationInfo;
		} else {
			throw new IllegalArgumentException("No auth found with uuid: " + uuid);
		}
	}

	public boolean existsByUuid(String uuid) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(uuid));
	}

	public boolean deleteAuth(String uuid) {
		Object object = redisTemplate.opsForValue().get(uuid);
		if (object instanceof AuthenticationInfo) {
			redisTemplate.delete(uuid);
		} else {
			return false;
		}
		return true;
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
		if (object instanceof DormantMembers dormantMembers) {
			return dormantMembers;
		} else {
			throw new IllegalArgumentException("No dormant found with uuid: " + uuid);
		}
	}
}