package store.novabook.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.dto.FindMemberLoginResponse;
import store.novabook.auth.dto.FindMembersRequest;
import store.novabook.auth.entity.AuthenticationMembers;
import store.novabook.auth.response.ApiResponse;

@Service
public class CustomAdminDetailsService implements UserDetailsService {

	private final CustomMembersDetailClient customMembersDetailClient;

	public CustomAdminDetailsService(CustomMembersDetailClient customMembersDetailClient) {
	    this.customMembersDetailClient = customMembersDetailClient;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		FindMembersRequest findMembersRequest = new FindMembersRequest(username);

		ApiResponse<FindMemberLoginResponse> findMemberLoginResponseResponse = customMembersDetailClient.findAdmin(
		    findMembersRequest);

		AuthenticationMembers authenticationMembers = new AuthenticationMembers();
		authenticationMembers.setId(findMemberLoginResponseResponse.getBody().id());
		authenticationMembers.setUsername(findMemberLoginResponseResponse.getBody().loginId());
		authenticationMembers.setPassword(findMemberLoginResponseResponse.getBody().password());
		authenticationMembers.setRole(findMemberLoginResponseResponse.getBody().role());
		return new CustomUserDetails(authenticationMembers);
	}
}