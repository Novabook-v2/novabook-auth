package store.novabook.auth.dto;

import lombok.Builder;

@Builder
public record LoginMemberRequest (String loginId, String loginPassword){
}
