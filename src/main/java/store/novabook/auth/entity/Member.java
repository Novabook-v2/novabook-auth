package store.novabook.auth.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.novabook.auth.dto.CreateMemberRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String loginId;

	@NotNull
	private String loginPassword;

	@NotNull
	private String name;

	@NotNull
	private String number;

	@NotNull
	private String email;

	@NotNull
	private LocalDateTime birth;

	@NotNull
	private LocalDateTime latestLoginAt;

	@NotNull
	private Long totalAmount;

	@NotNull
	private int authentication;

	@NotNull
	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "member_status_id")
	private MemberStatus memberStatus;

	@Builder
	public Member(String loginId,
		String loginPassword,
		String name,
		String number,
		String email,
		LocalDateTime birth,
		LocalDateTime latestLoginAt,
		Long totalAmount,
		int authentication,
		MemberStatus memberStatus) {
		this.loginId = loginId;
		this.loginPassword = loginPassword;
		this.name = name;
		this.number = number;
		this.email = email;
		this.birth = birth;
		this.latestLoginAt = latestLoginAt;
		this.totalAmount = totalAmount;
		this.authentication = authentication;
		this.memberStatus = memberStatus;
	}

	public void update(String loginPassword, String name, String number, String email) {
		this.loginPassword = loginPassword;
		this.name = name;
		this.number = number;
		this.email = email;
		this.updatedAt = LocalDateTime.now();
	}

	public void updateMemberStatus(MemberStatus memberStatus) {
		this.memberStatus = memberStatus;
	}

	public static Member of(CreateMemberRequest createMemberRequest, MemberStatus memberStatus, LocalDateTime birth) {
		return Member.builder()
			.memberStatus(memberStatus)
			.loginId(createMemberRequest.loginId())
			.loginPassword(createMemberRequest.loginPassword())
			.name(createMemberRequest.name())
			.number(createMemberRequest.number())
			.email(createMemberRequest.email())
			.birth(birth)
			.totalAmount(0L)
			.latestLoginAt(LocalDateTime.now())
			.build();
	}
}
