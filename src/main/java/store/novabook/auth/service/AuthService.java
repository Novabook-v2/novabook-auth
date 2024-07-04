package store.novabook.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.entity.Auth;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

	private final RedisTemplate<String, Object> redisTemplate;

	public void saveAuth(Auth auth) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(auth.getUuid()))) {
			throw new IllegalArgumentException("Auth already exists for this uuid: " + auth.getUuid());
		}
		redisTemplate.opsForValue().set(auth.getUuid(), auth);
	}

	public Auth getAuth(String uuid) {
		Object object = redisTemplate.opsForValue().get(uuid);
		if (object instanceof Auth) {
			return (Auth)object;
		} else {
			throw new IllegalArgumentException("No auth found with uuid: " + uuid);
		}
	}

	public boolean existsByUuid(String uuid) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(uuid));
	}

	public boolean deleteAuth(String uuid) {
		Object object = redisTemplate.opsForValue().get(uuid);
		if (object instanceof Auth) {
			redisTemplate.delete(uuid);
		} else {
			return false;
		}
		return true;
	}
}