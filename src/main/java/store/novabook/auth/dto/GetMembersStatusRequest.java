package store.novabook.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GetMembersStatusRequest(
	@NotBlank
	String accessToken) {
}
