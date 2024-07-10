package store.novabook.auth.util.dto;

public record JWTConfigDto (
	String header,
	String secret,
	int tokenValidityInSeconds
) {
}
