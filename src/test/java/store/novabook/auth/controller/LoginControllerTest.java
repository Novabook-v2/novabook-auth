package store.novabook.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
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

import store.novabook.auth.dto.request.LoginMembersRequest;
import store.novabook.auth.dto.response.LoginMembersResponse;
import store.novabook.auth.service.AuthenticationService;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc
class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthenticationService authenticationService;

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
	void testLogin() throws Exception {
		// given
		LoginMembersRequest loginRequest = new LoginMembersRequest("username", "password");
		LoginMembersResponse loginResponse = new LoginMembersResponse("accessToken", "refreshToken");
		given(authenticationService.login(loginRequest)).willReturn(loginResponse);

		// when & then
		String responseBody = mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andDo(print())  // 응답 본문을 출력합니다.
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		System.out.println("Response Body: " + responseBody);

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.accessToken").value("accessToken"))
			.andExpect(jsonPath("$.body.refreshToken").value("refreshToken"));
	}
}
