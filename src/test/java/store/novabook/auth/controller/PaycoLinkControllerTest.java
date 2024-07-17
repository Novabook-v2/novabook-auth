package store.novabook.auth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import store.novabook.auth.dto.request.LinkPaycoMembersUUIDRequest;
import store.novabook.auth.service.PaycoService;

@WebMvcTest(PaycoLinkController.class)
@AutoConfigureMockMvc
class PaycoLinkControllerTest {

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
	void testPaycoLinkSuccess() throws Exception {
		// given
		LinkPaycoMembersUUIDRequest request = new LinkPaycoMembersUUIDRequest("someUUID", "oauthId");
		given(paycoService.paycoLink(request)).willReturn(true);

		// when & then
		mockMvc.perform(post("/auth/payco/link")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	void testPaycoLinkFailure() throws Exception {
		// given
		LinkPaycoMembersUUIDRequest request = new LinkPaycoMembersUUIDRequest("someUUID", "oauthId");
		given(paycoService.paycoLink(request)).willReturn(false);

		// when & then
		mockMvc.perform(post("/auth/payco/link")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}
}
