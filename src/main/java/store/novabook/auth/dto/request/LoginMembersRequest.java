package store.novabook.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginMembersRequest(
	@NotBlank
	String loginId,
	@NotBlank
	String loginPassword
) {
}
