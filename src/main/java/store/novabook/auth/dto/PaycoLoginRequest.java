package store.novabook.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record PaycoLoginRequest(
	@NotBlank
	String paycoId
) {
}
