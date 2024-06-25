package store.novabook.auth.dto;

public record FindMemberLoginResponse(
	String loginId,
	String password,
	String role) {
}
