package store.novabook.auth.dto;

import lombok.Builder;

@Builder
public record LoginMemberResponse(boolean success, Long memberId, String name) {
}
