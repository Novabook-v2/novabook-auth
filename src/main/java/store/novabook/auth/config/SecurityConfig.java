package store.novabook.auth.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import store.novabook.auth.service.CustomAdminDetailsService;
import store.novabook.auth.service.CustomMembersDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomAdminDetailsService customAdminDetailsService;
	private final CustomMembersDetailsService customMembersDetailsService;

	public SecurityConfig(CustomAdminDetailsService customAdminDetailsService,
		CustomMembersDetailsService customMembersDetailsService) {
		this.customAdminDetailsService = customAdminDetailsService;
		this.customMembersDetailsService = customMembersDetailsService;
	}

	@Bean
	@Qualifier("adminAuthenticationManager")
	public AuthenticationManager adminAuthenticationManager() throws Exception {
		return new ProviderManager(Arrays.asList(adminAuthenticationProvider()));
	}

	@Bean
	@Primary
	@Qualifier("memberAuthenticationManager")
	public AuthenticationManager memberAuthenticationManager() throws Exception {
		return new ProviderManager(Arrays.asList(memberAuthenticationProvider()));
	}

	@Bean
	public DaoAuthenticationProvider adminAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customAdminDetailsService);
		provider.setPasswordEncoder(bCryptPasswordEncoder());
		return provider;
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
				.requestMatchers("/", "/auth/login", "/auth/uuid", "/auth/refresh").permitAll()
				.requestMatchers("/auth/admin/**").permitAll()
				.anyRequest().authenticated())
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}