package store.novabook.auth.dto.response;

public record GetMembersUUIDResponse(
	Long membersId,
	String role
) {
}
