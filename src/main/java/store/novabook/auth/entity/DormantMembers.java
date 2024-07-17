package store.novabook.auth.entity;

import org.springframework.data.redis.core.RedisHash;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@RedisHash("DormantMembers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DormantMembers {
	String uuid;
	Long membersId;

	@Builder
	private DormantMembers(String uuid, long membersId) {
		this.uuid = uuid;
		this.membersId = membersId;
	}

	public static DormantMembers of(String uuid, long membersId) {
		return DormantMembers.builder()
			.uuid(uuid)
			.membersId(membersId)
			.build();
	}
}
