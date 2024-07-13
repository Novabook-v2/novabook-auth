package store.novabook.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GetDormantMembersRequest(
	@NotBlank
	Long membersId) {
}
