package store.novabook.auth.dto.response;

public record LoginMembersResponse(
	String accessToken,
	String refreshToken
) {
}
