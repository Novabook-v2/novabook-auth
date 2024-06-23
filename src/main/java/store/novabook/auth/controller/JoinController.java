package store.novabook.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import store.novabook.auth.dto.JoinDTO;
import store.novabook.auth.dto.LoginMemberRequest;
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
	public String joinProcess(@RequestBody LoginMemberRequest loginMemberRequest) {
		// 외부 서비스에 요청을 보냅니다.
		String response = webClient.post()
			.uri("/login")
			.bodyValue(loginMemberRequest)
			.retrieve()
			.bodyToMono(String.class)
			.block();

		return response;
	}
	// @PostMapping("/join")
	// public String joinProcess(JoinDTO joinDTO) {
	//
	// 	System.out.println(joinDTO.getUsername());
	// 	// joinService.joinProcess(joinDTO);
	//
	// 	return "ok";
	// }
}