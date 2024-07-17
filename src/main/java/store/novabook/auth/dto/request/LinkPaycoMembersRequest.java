package store.novabook.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LinkPaycoMembersRequest(
	@NotBlank
	Long membersId,
	@NotBlank
	String oauthId
) {
}
