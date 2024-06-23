package store.novabook.auth.dto;

public record GetMemberAddressResponse(
	Long id,
	Long streetAddressId,
	Long memberId,
	String nickname,
	String streetAddress,
	String memberAddressDetail
) {

}
