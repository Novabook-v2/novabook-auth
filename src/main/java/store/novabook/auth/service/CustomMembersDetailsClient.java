package store.novabook.auth.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import store.novabook.auth.dto.request.GetDormantMembersRequest;
import store.novabook.auth.dto.response.GetDormantMembersResponse;
import store.novabook.auth.dto.request.GetPaycoMembersRequest;
import store.novabook.auth.dto.response.GetPaycoMembersResponse;
import store.novabook.auth.dto.request.LinkPaycoMembersRequest;
import store.novabook.auth.response.ApiResponse;
import store.novabook.auth.dto.response.FindMemberLoginResponse;
import store.novabook.auth.dto.request.FindMembersRequest;

@FeignClient(name = "gateway-service", path = "/api/v1/store/members", contextId = "customMembersDetailsClient")
public interface CustomMembersDetailsClient {

	@PostMapping("/find")
	ApiResponse<FindMemberLoginResponse> find(@Valid @RequestBody FindMembersRequest findMembersRequest);

	@PostMapping("/find/admin")
	ApiResponse<FindMemberLoginResponse> findAdmin(@Valid @RequestBody FindMembersRequest findMembersRequest);

	@PostMapping("/payco")
	ApiResponse<GetPaycoMembersResponse> getPaycoMembers(@Valid @RequestBody GetPaycoMembersRequest getPaycoMembersRequest);

	@PostMapping("/payco/link")
	ApiResponse<Void> linkPayco(@Valid @RequestBody LinkPaycoMembersRequest linkPaycoMembersRequest);

	@PostMapping("/status")
	ApiResponse<GetDormantMembersResponse> getMemberDormantStatus(@Valid @RequestBody GetDormantMembersRequest getDormantMembersRequest);
}
