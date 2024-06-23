package store.novabook.auth.dto;

public record LoginMemberResponse(boolean success, Long memberId, String name) {
}
