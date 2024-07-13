package store.novabook.auth.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import store.novabook.auth.entity.AuthenticationMembers;

public class CustomUserDetails implements UserDetails {

	private final AuthenticationMembers authenticationMembers;

	public CustomUserDetails(AuthenticationMembers authenticationMembers) {
		this.authenticationMembers = authenticationMembers;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return authenticationMembers.getRole();
			}
		});

		return collection;
	}
	public long getMembersId() {
		return authenticationMembers.getMembersId();
	}

	@Override
	public String getPassword() {
		return authenticationMembers.getLoginPassword();
	}

	@Override
	public String getUsername() {
		return authenticationMembers.getLoginId();
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

	public Object getDetails() {
		return authenticationMembers;
	}
}
