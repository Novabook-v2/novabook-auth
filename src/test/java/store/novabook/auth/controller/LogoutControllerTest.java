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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import store.novabook.auth.service.AuthenticationService;

@WebMvcTest(LogoutController.class)
@AutoConfigureMockMvc
class LogoutControllerTest {

	@Autowired
	private MockMvc mockMvc;

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
	void testLogoutSuccess() throws Exception {
		// given
		String accessToken = "someAccessToken";
		given(authenticationService.logout(accessToken)).willReturn(true);

		// when & then
		mockMvc.perform(post("/auth/logout")
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk());
	}

	@Test
	void testLogoutFailure() throws Exception {
		// given
		String accessToken = "someAccessToken";
		given(authenticationService.logout(accessToken)).willReturn(false);

		// when & then
		mockMvc.perform(post("/auth/logout")
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testLogoutWithoutToken() throws Exception {
		// when & then
		mockMvc.perform(post("/auth/logout"))
			.andExpect(status().isBadRequest());
	}
}
