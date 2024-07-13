package store.novabook.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PaycoLoginRequest(
	@NotBlank
	String paycoId
) {
}
