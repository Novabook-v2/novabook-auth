package store.novabook.auth.util;

import static store.novabook.auth.exception.ErrorCode.*;

import java.util.Map;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import store.novabook.auth.exception.KeyManagerException;
import store.novabook.auth.util.dto.JWTConfigDto;
import store.novabook.auth.util.dto.RedisConfigDto;

@Slf4j
public class KeyManagerUtil {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private KeyManagerUtil() {
	}

	private static String getDataSource(Environment environment, String keyid, RestTemplate restTemplate) {

		String appkey = environment.getProperty("nhn.cloud.keyManager.appkey");
		String userId = environment.getProperty("nhn.cloud.keyManager.userAccessKey");
		String secretKey = environment.getProperty("nhn.cloud.keyManager.secretAccessKey");

		String baseUrl = "https://api-keymanager.nhncloudservice.com/keymanager/v1.2/appkey/{appkey}/secrets/{keyid}";
		String url = baseUrl.replace("{appkey}", Objects.requireNonNull(appkey)).replace("{keyid}", keyid);
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-TC-AUTHENTICATION-ID", userId);
		headers.set("X-TC-AUTHENTICATION-SECRET", secretKey);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
			new ParameterizedTypeReference<>() {
			});

		return getStringObjectMap(response);
	}

	private static @NotNull String getStringObjectMap(ResponseEntity<Map<String, Object>> response) {
		if (response.getBody() == null) {
			throw new KeyManagerException(RESPONSE_BODY_IS_NULL);
		}
		Object bodyObj = response.getBody().get("body");

		Map<String, Object> body;
		try {
			body = TypeUtil.castMap(bodyObj, String.class, Object.class);
		} catch (ClassCastException e) {
			throw new KeyManagerException(MISSING_BODY_KEY);
		}

		String result = (String)body.get("secret");
		if (result.isEmpty()) {
			log.error("\"secret\" key is missing in responsxcle body");
			log.error("{}", body);
			throw new KeyManagerException(MISSING_BODY_KEY);
		}

		return result;
	}


	public static RedisConfigDto getRedisConfig(Environment environment, RestTemplate restTemplate) {
		try {
			String keyid = environment.getProperty("nhn.cloud.keyManager.redisKey");
			return objectMapper.readValue(getDataSource(environment, keyid, restTemplate), RedisConfigDto.class);
		} catch (JsonProcessingException e) {
			//오류처리
			log.error("RedisConfig{}", FAILED_CONVERSION.getMessage());
			throw new KeyManagerException(FAILED_CONVERSION);
		}
	}

	public static JWTConfigDto getJWTConfig(Environment environment, RestTemplate restTemplate) {
		try {
			String keyid = environment.getProperty("nhn.cloud.keyManager.jwtKey");
			return objectMapper.readValue(getDataSource(environment, keyid, restTemplate), JWTConfigDto.class);
		} catch (JsonProcessingException e) {
			//오류처리
			log.error("JWTConfig{}", FAILED_CONVERSION.getMessage());
			throw new KeyManagerException(FAILED_CONVERSION);
		}
	}

}
