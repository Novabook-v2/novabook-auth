package store.novabook.auth.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import store.novabook.auth.dto.JoinDTO;
import store.novabook.auth.dto.LoginMemberRequest;
import store.novabook.auth.dto.LoginMemberResponse;
import store.novabook.auth.service.JoinService;

@Controller
@ResponseBody
public class JoinController {

	private final JoinService joinService;
	private final WebClient webClient;

	public JoinController(JoinService joinService, WebClient.Builder webClientBuilder) {
		this.joinService = joinService;
		this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:9777/api/v1/store/members").build();

	}

	@PostMapping("/auth/join")
	public ResponseEntity<LoginMemberResponse> joinProcess(@RequestBody LoginMemberRequest loginMemberRequest) {
		// 외부 서비스에 요청을 보냅니다.
		String response = webClient.post()
			.uri("/login")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(loginMemberRequest))
			.retrieve()
			.bodyToMono(String.class)
			.block();

		LoginMemberResponse loginMemberResponse = LoginMemberResponse.builder()
			.success(true)
			.memberId(1L)
			.name("test")
			.build();

		return ResponseEntity.ok().body(loginMemberResponse);
	}

}