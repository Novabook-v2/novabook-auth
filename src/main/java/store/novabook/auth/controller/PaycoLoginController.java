package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.PaycoLoginRequest;
import store.novabook.auth.dto.response.PaycoLoginResponse;
import store.novabook.auth.service.PaycoService;

@RestController
@RequiredArgsConstructor()
@RequestMapping("/auth/payco")
public class PaycoLoginController {

	private final PaycoService paycoService;

	@PostMapping
	public ResponseEntity<PaycoLoginResponse> paycoLogin(@Valid @RequestBody PaycoLoginRequest paycoLoginRequest) {
		return ResponseEntity.ok().body(paycoService.paycoLogin(paycoLoginRequest));
	}
}
