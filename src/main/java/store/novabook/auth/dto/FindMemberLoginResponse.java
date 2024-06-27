package store.novabook.auth.dto;

public record FindMemberLoginResponse(
	long id,
	String loginId,
	String password,
	String role) {
}
