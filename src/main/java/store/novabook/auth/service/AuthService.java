package store.novabook.auth.service;

import java.util.NoSuchElementException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.entity.Auth;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

	private final RedisTemplate<String, Auth> redisTemplate;

	public void saveAuth(Auth auth) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(auth.getUuid()))) {
			throw new IllegalArgumentException("Auth already exists for this uuid: " + auth.getUuid());
		}
		redisTemplate.opsForValue().set(auth.getUuid(), auth);
	}

	public Auth getAuth(String id) {
		Auth auth = redisTemplate.opsForValue().get(id);
		if (auth == null) {
			throw new NoSuchElementException("No Auth found with id: " + id);
		}
		return auth;
	}
}