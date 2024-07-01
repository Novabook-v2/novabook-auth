package store.novabook.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import store.novabook.auth.entity.Auth;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Value("${spring.data.redis.password}")
	private String redisPassword;

	@Value("${spring.data.redis.database}")
	private int redisDatabase;

	private final ObjectMapper objectMapper;

	public RedisConfig(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
		lettuceConnectionFactory.setHostName(redisHost);
		lettuceConnectionFactory.setPort(redisPort);
		lettuceConnectionFactory.setPassword(redisPassword);
		lettuceConnectionFactory.setDatabase(redisDatabase);
		return lettuceConnectionFactory;
	}

	// @Bean
	// public RedisTemplate<String, Auth> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
	// 	RedisTemplate<String, Auth> redisTemplate = new RedisTemplate<>();
	// 	redisTemplate.setConnectionFactory(redisConnectionFactory);
	// 	redisTemplate.setKeySerializer(new StringRedisSerializer());
	// 	redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
	// 	return redisTemplate;
	// }

	// @Bean
	// public RedisTemplate<String, Auth> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
	// 	RedisTemplate<String, Auth> template = new RedisTemplate<>();
	//
	// 	template.setConnectionFactory(redisConnectionFactory);
	//
	// 	Jackson2JsonRedisSerializer<Auth> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Auth.class);
	// 	template.setValueSerializer(jackson2JsonRedisSerializer);
	// 	template.setKeySerializer(new StringRedisSerializer());
	//
	// 	template.afterPropertiesSet();
	//
	// 	return template;
	// }

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);

		// Jackson ObjectMapper 설정
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

		// JSON 직렬화기 설정
		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		// RedisTemplate에 직렬화기 설정
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(serializer);

		template.afterPropertiesSet();
		return template;
	}

}
