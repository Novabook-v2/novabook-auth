package store.novabook.auth.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import store.novabook.auth.service.CustomMembersDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomMembersDetailsService customMembersDetailsService;

	public SecurityConfig(
		CustomMembersDetailsService customMembersDetailsService) {
		this.customMembersDetailsService = customMembersDetailsService;
	}

	@Bean
	public AuthenticationManager memberAuthenticationManager() {
		return new ProviderManager(Collections.singletonList(memberAuthenticationProvider()));
	}

	@Bean
	public DaoAuthenticationProvider memberAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customMembersDetailsService);
		provider.setPasswordEncoder(bCryptPasswordEncoder());
		return provider;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/", "/auth/login", "/auth/members/uuid", "/auth/refresh", "/auth/admin/login",
					"/auth/members/token", "/auth/logout", "/auth/payco", "/auth/members/status",
					"/auth/members/uuid/dormant",
					"/auth/payco/link").permitAll()
				.anyRequest().authenticated())
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}