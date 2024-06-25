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

	// private final UserRepository userRepository;
	// private final WebClient webClient;
	private final CustomUserDetailClient customUserDetailClient;

	public CustomUserDetailsService(CustomUserDetailClient customUserDetailClient) {
		// this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:9777/api/v1/store/members").build();
		this.customUserDetailClient = customUserDetailClient;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		FindMemberRequest findMemberRequest = new FindMemberRequest(username);
		// String response = webClient.post()
		// 	.uri("/find")
		// 	.bodyValue(findMemberRequest)
		// 	.retrieve()
		// 	.bodyToMono(String.class)
		// 	.block();

		ApiResponse<FindMemberLoginResponse> findMemberLoginResponseResponseEntity = customUserDetailClient.find(
			findMemberRequest);

		// {"body":{"loginId":"1","password":"$2a$12$upR0O26.wR3mfB7YUBScYuSPE5ESv01xvPbYYeJUsgrVyp1PiQ/sO","role":null},"header":{"isSuccessful":true,"resultMessage":"SUCCESS"}}

		// ApiResponse<FindMemberLoginResponse> response2 = webClient.post()
		// 	.uri("/find")
		// 	.bodyValue(findMemberRequest)
		// 	.retrieve()
		// 	.bodyToMono(FindMemberLoginResponse.class)
		// 	.block();

		// Member2 member = webClient.post()
		// 	.uri("/find", username)
		// 	.bodyValue(findMemberRequest)
		// 	.retrieve()
		// 	.bodyToMono(Member2.class)
		// 	.block();

		Member2 member = new Member2();
		member.setUsername("1");
		member.setPassword("$2a$12$U7fUE2izybNwQqaWXENywuAa45DoPPW/ZeS56g0iFzGDi0jKYGUOW");
		member.setRole("ROLE_USER");
		return new CustomUserDetails(member);
		// if (!response.isEmpty()) {
		// }

		// new CustomUserDetails()

		//DB에서 조회
		// UserEntity userData = userRepository.findByUsername(username);
		//
		// if (userData != null) {
		//
		// 	//UserDetails에 담아서 return하면 AutneticationManager가 검증 함
		// 	return new CustomUserDetails(userData);
		// }

		// return null;
	}
}
