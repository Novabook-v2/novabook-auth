package store.novabook.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GetPaycoMembersRequest(
	@NotBlank
	String paycoId
) {
}
