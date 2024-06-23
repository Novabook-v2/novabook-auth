package store.novabook.auth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.entity.Member;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	// private final UserRepository userRepository;
	private final WebClient webClient;

	public CustomUserDetailsService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:9777/api/v1/store/members").build();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		//DB에서 조회
		// UserEntity userData = userRepository.findByUsername(username);
		//
		// if (userData != null) {
		//
		// 	//UserDetails에 담아서 return하면 AutneticationManager가 검증 함
		// 	return new CustomUserDetails(userData);
		// }

		// String response = webClient.post()
		// 	.uri("/login")
		// 	.bodyValue(loginMemberRequest)
		// 	.retrieve()
		// 	.bodyToMono(String.class)
		// 	.block();

		return null;
	}



	// private org.springframework.security.core.userdetails.User createUser(String username, User user) {
	// 	if (!user.isActivated()) {
	// 		throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
	// 	}
	//
	// 	List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
	// 		.map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
	// 		.collect(Collectors.toList());
	//
	// 	return new org.springframework.security.core.userdetails.User(user.getUsername(),
	// 		user.getPassword(),
	// 		grantedAuthorities);
	// }
}
