package store.novabook.auth.jwt;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.entity.Auth;
import store.novabook.auth.service.AuthService;

@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {

	private Key key;

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.token-validity-in-seconds}")
	private long tokenValidityInSeconds;

	@Override
	public void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	private final AuthService authService;

	public String createToken(Authentication authentication) {

		Date now = new Date();
		Date validity = new Date(now.getTime() + tokenValidityInSeconds * 1000);

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		String authoritiesString = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		UUID uuid = UUID.randomUUID();
		Auth auth = Auth.of(uuid.toString(), Long.parseLong(authentication.getName()), authoritiesString);
		authService.saveAuth(auth);

		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim("uuid", uuid.toString())
			.claim("authorities", authoritiesString)
			.signWith(key, SignatureAlgorithm.HS256)
			.setIssuedAt(now)
			.setExpiration(validity)
			.compact();
	}

	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();

		return claims.getSubject();
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
