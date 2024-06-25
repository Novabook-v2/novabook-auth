package store.novabook.auth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import store.novabook.auth.config.ApiResponse;
import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.dto.FindMemberLoginResponse;
import store.novabook.auth.dto.FindMemberRequest;
import store.novabook.auth.entity.Member;
import store.novabook.auth.entity.Member2;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final CustomUserDetailClient customUserDetailClient;

	public CustomUserDetailsService(CustomUserDetailClient customUserDetailClient) {
		this.customUserDetailClient = customUserDetailClient;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		FindMemberRequest findMemberRequest = new FindMemberRequest(username);

		ApiResponse<FindMemberLoginResponse> findMemberLoginResponseResponseEntity = customUserDetailClient.find(
			findMemberRequest);

		Member2 member = new Member2();
		member.setUsername("1");
		member.setPassword("$2a$12$U7fUE2izybNwQqaWXENywuAa45DoPPW/ZeS56g0iFzGDi0jKYGUOW");
		member.setRole("ROLE_USER");
		return new CustomUserDetails(member);
	}
}
