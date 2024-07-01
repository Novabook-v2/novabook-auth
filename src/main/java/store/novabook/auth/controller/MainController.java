package store.novabook.auth.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.novabook.auth.entity.Auth;
import store.novabook.auth.service.AuthService;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class MainController {

	private final AuthService authService;


	@GetMapping("/")
	public String mainP() {
		UUID uuid = UUID.randomUUID();

		Auth auth = Auth.of(uuid.toString(), 1L, "ROLE_USER", LocalDateTime.now());
		authService.saveAuth(auth);

		authService.getAuth(uuid.toString());
		return "main Controller";
	}
}