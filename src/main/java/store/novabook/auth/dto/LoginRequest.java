package store.novabook.auth.dto;

public record LoginRequest(
	String username,
	String password
) {
}
