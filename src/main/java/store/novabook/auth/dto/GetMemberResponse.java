package store.novabook.auth.dto;

import store.novabook.auth.entity.Member;

public record GetMemberResponse(
	Long id,
	String loginId,
	String name,
	String email
) {
	public static GetMemberResponse fromEntity(Member member) {
		return new GetMemberResponse(
			member.getId(),
			member.getLoginId(),
			member.getName(),
			member.getEmail());
	}
}
