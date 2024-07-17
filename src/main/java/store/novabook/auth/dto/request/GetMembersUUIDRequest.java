package store.novabook.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GetMembersUUIDRequest(
	@NotBlank
	String uuid
) {
}
