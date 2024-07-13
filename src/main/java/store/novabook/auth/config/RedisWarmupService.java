package store.novabook.auth.config;

import java.time.Duration;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisWarmupService {

	private final RedisTemplate<String, Object> redisTemplate;

	@EventListener(ApplicationReadyEvent.class)
	public void warmup() {
		redisTemplate.opsForValue().set("warmup-key1", "warmup-value1", Duration.ofMinutes(10));
		redisTemplate.opsForValue().set("warmup-key2", "warmup-value2", Duration.ofMinutes(20));
	}
}
