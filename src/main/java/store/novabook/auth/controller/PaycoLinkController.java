package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.LinkPaycoMembersUUIDRequest;
import store.novabook.auth.service.PaycoService;

@RestController
@RequiredArgsConstructor()
@RequestMapping("/auth/payco/link")
public class PaycoLinkController {

	private final PaycoService paycoService;

	@PostMapping
	public ResponseEntity<Void> paycoLink(@Valid @RequestBody LinkPaycoMembersUUIDRequest linkPaycoMembersUUIDRequest) {
		if (paycoService.paycoLink(linkPaycoMembersUUIDRequest)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.badRequest().build();
		}
	}
}
