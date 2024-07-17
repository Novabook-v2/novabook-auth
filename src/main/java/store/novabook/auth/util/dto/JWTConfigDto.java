package store.novabook.auth.util.dto;

import lombok.Builder;

@Builder
public record JWTConfigDto (
	String header,
	String secret,
	int tokenValidityInSeconds
) {
}
