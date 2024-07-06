package store.novabook.auth.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisHash;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@RedisHash("AuthenticationInfo")
public class AuthenticationInfo implements Serializable {

	@NotNull
	String uuid;

	@NotNull
	long membersId;

	@NotNull
	String role;

	LocalDateTime expirationTime;

	@Builder
	private AuthenticationInfo(String uuid, long membersId, String role, LocalDateTime expirationTime) {
		this.uuid = uuid;
		this.membersId = membersId;
		this.role = role;
		this.expirationTime = expirationTime;
	}

	public static AuthenticationInfo of(String uuid, long membersId, String role, LocalDateTime expirationTime) {
		return AuthenticationInfo.builder().uuid(uuid).membersId(membersId).role(role).expirationTime(expirationTime).build();
	}
}