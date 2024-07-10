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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.util.KeyManagerUtil;
import store.novabook.auth.util.dto.RedisConfigDto;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {
	private final Environment env;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisConfigDto redisConfig = KeyManagerUtil.getRedisConfig(env);

		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(redisConfig.host());
		config.setPort(redisConfig.port());
		config.setPassword(RedisPassword.of(redisConfig.password()));
		config.setDatabase(redisConfig.database());

		return new LettuceConnectionFactory(config);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
			.allowIfBaseType(Object.class)
			.build();

		objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);

		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(serializer);

		template.afterPropertiesSet();
		return template;
	}

}
