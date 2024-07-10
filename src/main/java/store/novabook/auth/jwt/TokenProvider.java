package store.novabook.auth.jwt;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.dto.GetPaycoMembersResponse;
import store.novabook.auth.entity.AuthenticationInfo;
import store.novabook.auth.service.AuthenticationService;
import store.novabook.auth.util.KeyManagerUtil;
import store.novabook.auth.util.dto.JWTConfigDto;

@Component
public class TokenProvider implements InitializingBean {

	private Key key;

	private final JWTConfigDto jwt;

	private static final String AUTHORITIES = "authorities";
	private static final String UUID = "uuid";
	private static final String CATEGORY = "category";
	private static final String ACCESS = "access";

	private final AuthenticationService authenticationService;

	public TokenProvider(AuthenticationService authenticationService, Environment env) {
		this.authenticationService = authenticationService;
		this.jwt = KeyManagerUtil.getJWTConfig(env);
	}

	@Override
	public void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(jwt.secret());
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}



	public String createAccessToken(Authentication authentication, UUID uuid) {

		Date now = new Date();
		// Date validity = new Date(now.getTime() + jwt.tokenValidityInSeconds() * 1000);
		Date validity = new Date(now.getTime() + 30 * 1000);

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		String authoritiesString = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.claim(AUTHORITIES, authoritiesString)
			.claim(CATEGORY, ACCESS)
			.signWith(key, SignatureAlgorithm.HS256)
			.setIssuedAt(now)
			.setExpiration(validity)
			.compact();
	}

	public String createAccessToken(UUID uuid) {

		Date now = new Date();
		// Date validity = new Date(now.getTime() + tokenValidityInSeconds * 1000);
		Date validity = new Date(now.getTime() + 30 * 1000);

		String authoritiesString = "ROLE_USER";

		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.claim(AUTHORITIES, authoritiesString)
			.claim(CATEGORY, ACCESS)
			.signWith(key, SignatureAlgorithm.HS256)
			.setIssuedAt(now)
			.setExpiration(validity)
			.compact();
	}

	public String createRefreshToken(Authentication authentication, UUID uuid) {

		Date now = new Date();
		Date validity = new Date(now.getTime() + 6000 * 1000);

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		String authoritiesString = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		AuthenticationInfo authenticationInfo = AuthenticationInfo.of(uuid.toString(), Long.parseLong(authentication.getName()), authoritiesString,
			LocalDateTime.ofInstant(validity.toInstant(), ZoneId.systemDefault()));
		authenticationService.saveAuth(authenticationInfo);

		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.claim(AUTHORITIES, authoritiesString)
			.claim(CATEGORY, "refresh")
			.signWith(key, SignatureAlgorithm.HS256)
			.setIssuedAt(now)
			.setExpiration(validity)
			.compact();
	}

	public String createRefreshToken(GetPaycoMembersResponse getPaycoMembersResponse, UUID uuid) {

		Date now = new Date();
		Date validity = new Date(now.getTime() + 6000 * 1000);

		AuthenticationInfo authenticationInfo = AuthenticationInfo.of(uuid.toString(), getPaycoMembersResponse.id(), "ROLE_MEMBERS",
			LocalDateTime.ofInstant(validity.toInstant(), ZoneId.systemDefault()));
		authenticationService.saveAuth(authenticationInfo);

		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.claim(AUTHORITIES, "ROLE_MEMBERS")
			.claim(CATEGORY, "refresh")
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

		return claims.get("uuid", String.class);
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
