package store.novabook.auth.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.dto.LoginRequest;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;

	private final ObjectMapper objectMapper = new ObjectMapper();

	// private final WebClient webClient;

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		// try {
		// 	StringBuilder builder = new StringBuilder();
		// 	String line;
		// 	BufferedReader reader = request.getReader();
		// 	while ((line = reader.readLine()) != null) {
		// 		builder.append(line);
		// 	}
		// 	String body = builder.toString();
		// 	// 이제 body 변수에 요청 본문이 저장되어 있습니다.
		// 	// 필요에 따라 이 값을 사용하세요.
		// } catch (IOException e) {
		// 	// 요청 본문을 읽는 도중 오류가 발생했습니다.
		// 	// 적절한 오류 처리를 수행하세요.
		// }
		LoginRequest loginRequest = null;
		try {
			loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
			// UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password(), null);
			// return this.getAuthenticationManager().authenticate(authRequest);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		//클라이언트 요청에서 username, password 추출
		// String username = obtainUsername(request);
		// String password = obtainPassword(request);
		//
		// //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password(),
			null);


		//loadUserByUsername 메소드 실행
		//token에 담은 검증을 위한 AuthenticationManager로 전달
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) {

		//UserDetailsS
		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		// GrantedAuthority auth = iterator.next();

		// String role = auth.getAuthority();
		String role = "ROLE_USER";

		String token = jwtUtil.createJwt(username, role, 60 * 60 * 10L);

		response.addHeader("Authorization", "Bearer " + token);
		try {
			chain.doFilter(request, response);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}

	//로그인 실패시 실행하는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) {
		response.setStatus(401);

	}
}