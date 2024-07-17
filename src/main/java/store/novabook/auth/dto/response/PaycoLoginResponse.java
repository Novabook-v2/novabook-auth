package store.novabook.auth.dto.response;

public record PaycoLoginResponse(
	String accessToken,
	String refreshToken) {
}
