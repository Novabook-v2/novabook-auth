package store.novabook.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.novabook.auth.dto.request.GetNewTokenRequest;
import store.novabook.auth.dto.response.GetNewTokenResponse;
import store.novabook.auth.service.AuthenticationService;

@WebMvcTest(RefreshController.class)
@AutoConfigureMockMvc
class RefreshControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AuthenticationService authenticationService;

	@TestConfiguration
	static class TestSecurityConfig {
		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize
					.anyRequest().permitAll() // 모든 요청을 허용합니다.
				);
			return http.build();
		}
	}

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetNewToken() throws Exception {
		// given
		GetNewTokenRequest getNewTokenRequest = new GetNewTokenRequest("refreshToken");
		GetNewTokenResponse getNewTokenResponse = new GetNewTokenResponse("newAccessToken");

		given(authenticationService.createNewToken(getNewTokenRequest)).willReturn(getNewTokenResponse);

		// when & then
		mockMvc.perform(post("/auth/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(getNewTokenRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.accessToken").value("newAccessToken"));
	}
}
