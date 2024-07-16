package store.novabook.auth.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
public class RefreshTokenInfo implements Serializable {
	@NotNull
	private String uuid;

	@NotNull
	private String accessTokenUUID;

	@NotNull
	long membersId;

	@NotNull
	private String role;

	@NotNull
	private LocalDateTime expirationTime;

	@NotNull
	private LocalDateTime createdTime;

	public RefreshTokenInfo(String uuid, String accessTokenUUID, long membersId, String role,
		LocalDateTime expirationTime,
		LocalDateTime createdTime) {
		this.uuid = uuid;
		this.accessTokenUUID = accessTokenUUID;
		this.membersId = membersId;
		this.role = role;
		this.expirationTime = expirationTime;
		this.createdTime = createdTime;
	}

	public static RefreshTokenInfo of(String uuid, String accessTokenUUID, long membersId, String role,
		LocalDateTime expirationTime) {
		return new RefreshTokenInfo(uuid, accessTokenUUID, membersId, role, expirationTime, LocalDateTime.now());
	}
}
