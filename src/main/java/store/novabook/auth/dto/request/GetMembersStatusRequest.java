package store.novabook.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GetMembersStatusRequest(
	@NotBlank
	String accessToken) {
}
