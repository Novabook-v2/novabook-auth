package store.novabook.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.dto.response.FindMemberLoginResponse;
import store.novabook.auth.dto.request.FindMembersRequest;
import store.novabook.auth.entity.AuthenticationMembers;
import store.novabook.auth.response.ApiResponse;

public class CustomMembersDetailsServiceTest {

	@Mock
	private CustomMembersDetailsClient customMembersDetailsClient;

	@InjectMocks
	private CustomMembersDetailsService customMembersDetailsService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testLoadUserByUsername() {
		// given
		String username = "testUser";
		long membersId = 1L;
		String loginPassword = "testPassword";
		String role = "ROLE_USER";

		FindMemberLoginResponse findMemberLoginResponse = new FindMemberLoginResponse(
			membersId, username, loginPassword, role
		);

		ApiResponse<FindMemberLoginResponse> apiResponse = ApiResponse.success(findMemberLoginResponse);

		given(customMembersDetailsClient.find(new FindMembersRequest(username)))
			.willReturn(apiResponse);

		// when
		UserDetails userDetails = customMembersDetailsService.loadUserByUsername(username);

		// then
		assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
		CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

		assertThat(customUserDetails.getMembersId()).isEqualTo(membersId);
		assertThat(customUserDetails.getUsername()).isEqualTo(username);
		assertThat(customUserDetails.getPassword()).isEqualTo(loginPassword);
		assertThat(customUserDetails.getRole()).isEqualTo(role);
	}

	@Test
	void testLoadUserByUsername_ThrowsUsernameNotFoundException() {
		// given
		String username = "invalidUser";

		// Mock the client to return an error response with no body
		given(customMembersDetailsClient.find(new FindMembersRequest(username)))
			.willReturn(ApiResponse.error(null));

		// when & then
		assertThatThrownBy(() -> customMembersDetailsService.loadUserByUsername(username))
			.isInstanceOf(UsernameNotFoundException.class);
	}
}
