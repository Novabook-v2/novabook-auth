package store.novabook.auth.service;

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

		String response = webClient.post()
			.uri("/login")
			.bodyValue(loginMemberRequest)
			.retrieve()
			.bodyToMono(String.class)
			.block();

		return null;
	}
}
