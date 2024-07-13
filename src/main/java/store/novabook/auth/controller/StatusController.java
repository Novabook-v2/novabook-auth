package store.novabook.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.request.GetDormantMembersRequest;
import store.novabook.auth.dto.response.GetDormantMembersResponse;
import store.novabook.auth.dto.request.GetMembersStatusRequest;
import store.novabook.auth.dto.response.GetMembersStatusResponse;
import store.novabook.auth.entity.DormantMembers;
import store.novabook.auth.jwt.TokenProvider;
import store.novabook.auth.response.ApiResponse;
import store.novabook.auth.service.AuthenticationService;
import store.novabook.auth.service.CustomMembersDetailsClient;

@RestController
@RequestMapping("/auth/members/status")
@RequiredArgsConstructor
public class StatusController {

	private final AuthenticationService authenticationService;
	private final CustomMembersDetailsClient customMembersDetailsClient;
	private final TokenProvider tokenProvider;

	@PostMapping
	public ResponseEntity<GetMembersStatusResponse> status(
		@Valid @RequestBody GetMembersStatusRequest getMembersStatusRequest) {

		String uuid = tokenProvider.getUsernameFromToken(getMembersStatusRequest.accessToken());
		long membersId = authenticationService.getAuth(uuid).getMembersId();
		GetDormantMembersRequest getDormantMembersRequest = new GetDormantMembersRequest(membersId);

		ApiResponse<GetDormantMembersResponse> getDormantMembersResponse = customMembersDetailsClient.getMemberDormantStatus(
			getDormantMembersRequest);
		if (getDormantMembersResponse == null) {
			return ResponseEntity.badRequest().build();
		}
		if (getDormantMembersResponse.getBody().memberStatusId() == 2) {
			DormantMembers dormantMembers = DormantMembers.of(uuid, membersId);
			authenticationService.saveDormant(dormantMembers);
			return ResponseEntity.ok(
				new GetMembersStatusResponse(getDormantMembersResponse.getBody().memberStatusId(), uuid));
		}

		return ResponseEntity.ok(
			new GetMembersStatusResponse(getDormantMembersResponse.getBody().memberStatusId(), null));
	}
}
