package store.novabook.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import store.novabook.auth.response.ApiResponse;
import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.dto.FindMemberLoginResponse;
import store.novabook.auth.dto.FindMembersRequest;
import store.novabook.auth.entity.Users;

@Service
public class CustomMembersDetailsService implements UserDetailsService {

	private final CustomMembersDetailClient customMembersDetailClient;

	public CustomMembersDetailsService(CustomMembersDetailClient customMembersDetailClient) {
		this.customMembersDetailClient = customMembersDetailClient;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		FindMembersRequest findMembersRequest = new FindMembersRequest(username);

		ApiResponse<FindMemberLoginResponse> findMembersLoginResponseResponse = customMembersDetailClient.find(
			findMembersRequest);

		Users users = new Users();
		users.setId(findMembersLoginResponseResponse.getBody().id());
		users.setUsername(findMembersLoginResponseResponse.getBody().loginId());
		users.setPassword(findMembersLoginResponseResponse.getBody().password());
		users.setRole(findMembersLoginResponseResponse.getBody().role());
		return new CustomUserDetails(users);
	}
}