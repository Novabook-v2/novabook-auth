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

import store.novabook.auth.dto.request.GetMembersUUIDRequest;
import store.novabook.auth.dto.response.GetDormantMembersUUIDResponse;
import store.novabook.auth.dto.response.GetMembersTokenResponse;
import store.novabook.auth.dto.response.GetMembersUUIDResponse;
import store.novabook.auth.service.UUIDService;

@WebMvcTest(UUIDController.class)
@AutoConfigureMockMvc
class UUIDControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UUIDService uuidService;

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
	void testGetMembersId() throws Exception {
		// given
		GetMembersUUIDRequest request = new GetMembersUUIDRequest("someId");
		GetMembersUUIDResponse response = new GetMembersUUIDResponse(1L, "ROLE_MEMBERS");

		given(uuidService.getMembersUUID(request)).willReturn(response);

		// when & then
		mockMvc.perform(post("/auth/members/uuid")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.membersId").value(1L));
	}

	@Test
	void testGetDormantMembersId() throws Exception {
		// given
		GetMembersUUIDRequest request = new GetMembersUUIDRequest("someId");
		GetDormantMembersUUIDResponse response = new GetDormantMembersUUIDResponse(1L);

		given(uuidService.getDormantMembersId(request)).willReturn(response);

		// when & then
		mockMvc.perform(post("/auth/members/uuid/dormant")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.membersId").value(1L));
	}

	@Test
	void testMembersToken() throws Exception {
		// given
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";
		GetMembersTokenResponse response = new GetMembersTokenResponse(1L);

		given(uuidService.getMembersToken(accessToken, refreshToken)).willReturn(response);

		// when & then
		mockMvc.perform(post("/auth/members/token")
				.header("authorization", "Bearer " + accessToken)
				.header("refresh", "Bearer " + refreshToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.membersId").value(1L));
	}
}
