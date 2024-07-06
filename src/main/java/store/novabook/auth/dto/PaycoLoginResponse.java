package store.novabook.auth.dto;

public record PaycoLoginResponse(
	String accessToken,
	String refreshToken) {
}
