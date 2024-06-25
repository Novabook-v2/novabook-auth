package store.novabook.auth.dto;

import lombok.Builder;
import store.novabook.auth.entity.Member;

@Builder
public record CreateMemberResponse(Long id) {
	public static CreateMemberResponse fromEntity(Member member) {
		return CreateMemberResponse.builder()
			.id(member.getId())
			.build();
	}
}
