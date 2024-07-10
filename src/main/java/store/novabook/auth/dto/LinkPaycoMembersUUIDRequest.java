package store.novabook.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LinkPaycoMembersUUIDRequest(
	@NotBlank
	String accessToken,
	@NotBlank
	String oauthId
) {
}
