package store.novabook.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GetNewTokenRequest(
	@NotBlank
	String refreshToken
) {
}
