package store.novabook.auth.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.novabook.auth.config.ApiResponse;
import store.novabook.auth.dto.FindMemberLoginResponse;
import store.novabook.auth.dto.FindMemberRequest;

@FeignClient(name = "customUserDetailClient", url = "http://127.0.0.1:9777/api/v1/store/members")
public interface CustomUserDetailClient {

	@PostMapping("/find")
	ApiResponse<FindMemberLoginResponse> find(@RequestBody FindMemberRequest findMemberRequest);
}
