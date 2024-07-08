package store.novabook.auth.util.dto;

public record RedisConfigDto(
	String host,
	int database,
	String password,
	int port
) {
}
