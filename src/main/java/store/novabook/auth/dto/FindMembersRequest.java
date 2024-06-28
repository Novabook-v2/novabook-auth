package store.novabook.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record FindMembersRequest(
	@NotBlank
	String loginId
) {
}
