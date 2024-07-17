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

import store.novabook.auth.dto.request.PaycoLoginRequest;
import store.novabook.auth.dto.response.PaycoLoginResponse;
import store.novabook.auth.service.PaycoService;

@WebMvcTest(PaycoLoginController.class)
@AutoConfigureMockMvc
class PaycoLoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PaycoService paycoService;

	@Autowired
	private ObjectMapper objectMapper;

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
	void testPaycoLoginSuccess() throws Exception {
		// given
		PaycoLoginRequest loginRequest = new PaycoLoginRequest("username");
		PaycoLoginResponse loginResponse = new PaycoLoginResponse("accessToken", "refreshToken");
		given(paycoService.paycoLogin(loginRequest)).willReturn(loginResponse);

		// when & then
		mockMvc.perform(post("/auth/payco")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.accessToken").value("accessToken"))
			.andExpect(jsonPath("$.body.refreshToken").value("refreshToken"));
	}

	@Test
	void testPaycoLoginFailure() throws Exception {
		// given
		PaycoLoginRequest loginRequest = new PaycoLoginRequest("username");
		given(paycoService.paycoLogin(loginRequest)).willReturn(null);

		// when & then
		mockMvc.perform(post("/auth/payco")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk());
	}
}
