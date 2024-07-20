package store.novabook.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.util.KeyManagerUtil;
import store.novabook.auth.util.dto.RedisConfigDto;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {
	private final Environment env;
	private final ObjectMapper objectMapper;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RestTemplate restTemplate = new RestTemplate();
		RedisConfigDto redisConfig = KeyManagerUtil.getRedisConfig(env, restTemplate);

		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(redisConfig.host());
		config.setPort(redisConfig.port());
		config.setPassword(RedisPassword.of(redisConfig.password()));
		config.setDatabase(redisConfig.database());

		return new LettuceConnectionFactory(config);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
		return template;
	}
}
