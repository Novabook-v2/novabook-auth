package store.novabook.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FindMembersRequest(
	@NotBlank
	String loginId
) {
}
