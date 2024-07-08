package store.novabook.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisWarmupService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@EventListener(ApplicationReadyEvent.class)
	public void warmup() {
		redisTemplate.opsForValue().set("key1", "value1", Duration.ofMinutes(10));
		redisTemplate.opsForValue().set("key2", "value2", Duration.ofMinutes(20));
	}
}
