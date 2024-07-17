package store.novabook.auth.dto.response;

public record FindMemberLoginResponse(
	long membersId,
	String loginId,
	String loginPassword,
	String role) {
}
