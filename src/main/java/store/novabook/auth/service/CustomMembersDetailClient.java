package store.novabook.auth.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import store.novabook.auth.response.ApiResponse;
import store.novabook.auth.dto.FindMemberLoginResponse;
import store.novabook.auth.dto.FindMembersRequest;

@FeignClient(name = "customUserDetailClient", url = "http://127.0.0.1:9777/api/v1/store/members")
public interface CustomMembersDetailClient {

	@PostMapping("/find")
	ApiResponse<FindMemberLoginResponse> find(@Valid @RequestBody FindMembersRequest findMembersRequest);

	@PostMapping("/find/admin")
	ApiResponse<FindMemberLoginResponse> findAdmin(@Valid @RequestBody FindMembersRequest findMembersRequest);
}
