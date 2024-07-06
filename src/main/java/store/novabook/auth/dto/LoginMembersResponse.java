package store.novabook.auth.dto;

public record LoginMembersResponse(
	String accessToken,
	String refreshToken
) {
}
