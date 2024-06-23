package store.novabook.auth.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import store.novabook.auth.entity.Member;
import store.novabook.auth.entity.Users;

public class CustomUserDetails implements UserDetails {

	private final Member member;

	public CustomUserDetails(Member member) {
		this.member = member;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> collection = new ArrayList<>();
		// collection.add(new GrantedAuthority() {
		// 	@Override
		// 	public String getAuthority() {
		// 		return userEntity.getRole();
		// 	}
		// });

		return collection;
	}

	@Override
	public String getPassword() {
		return member.getLoginPassword();
	}

	@Override
	public String getUsername() {
		return member.getName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
