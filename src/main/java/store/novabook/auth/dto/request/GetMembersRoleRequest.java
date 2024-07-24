package store.novabook.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GetMembersRoleRequest(
	@NotBlank
	String accessToken
) {
}
