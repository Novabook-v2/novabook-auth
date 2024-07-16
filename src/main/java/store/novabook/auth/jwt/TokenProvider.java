package store.novabook.auth.jwt;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import store.novabook.auth.dto.CustomUserDetails;
import store.novabook.auth.dto.response.GetPaycoMembersResponse;
import store.novabook.auth.entity.AccessTokenInfo;
import store.novabook.auth.entity.AuthenticationInfo;
import store.novabook.auth.entity.RefreshTokenInfo;
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
	private static final String ROLE_MEMBERS = "ROLE_MEMBERS";

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
		Date validity = new Date(now.getTime() + 60 * 1000);

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

	public String createAccessToken(UUID uuid, String authorities) {

		Date now = new Date();
		Date validity = new Date(now.getTime() + 60 * 1000);

		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.claim(AUTHORITIES, authorities)
			.claim(CATEGORY, ACCESS)
			.signWith(key, SignatureAlgorithm.HS256)
			.setIssuedAt(now)
			.setExpiration(validity)
			.compact();
	}

	public String createOauthAccessToken(UUID uuid) {

		Date now = new Date();
		Date validity = new Date(now.getTime() + 60000 * 1000);

		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.claim(AUTHORITIES, ROLE_MEMBERS)
			.claim(CATEGORY, ACCESS)
			.signWith(key, SignatureAlgorithm.HS256)
			.setIssuedAt(now)
			.setExpiration(validity)
			.compact();
	}

	public String createAccessToken(UUID uuid) {
		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String createRefreshToken(UUID uuid) {
		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String createRefreshToken(Authentication authentication, UUID uuid) {

		Date now = new Date();
		Date validity = new Date(now.getTime() + 60000 * 1000);

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		String authoritiesString = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		CustomUserDetails principal = (CustomUserDetails)authentication.getPrincipal();

		AuthenticationInfo authenticationInfo = AuthenticationInfo.of(uuid.toString(), principal.getMembersId(),
			authoritiesString,
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

	public String createOauthRefreshToken(GetPaycoMembersResponse getPaycoMembersResponse, UUID uuid) {

		Date now = new Date();
		Date validity = new Date(now.getTime() + 60000 * 1000);

		AuthenticationInfo authenticationInfo = AuthenticationInfo.of(uuid.toString(), getPaycoMembersResponse.id(),
			ROLE_MEMBERS,
			LocalDateTime.ofInstant(validity.toInstant(), ZoneId.systemDefault()));
		authenticationService.saveAuth(authenticationInfo);

		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.claim(AUTHORITIES, ROLE_MEMBERS)
			.claim(CATEGORY, "refresh")
			.signWith(key, SignatureAlgorithm.HS256)
			.setIssuedAt(now)
			.setExpiration(validity)
			.compact();
	}

	public String getUUID(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
			return claims.get("uuid", String.class);
		} catch (JwtException e) {

		}
		return null;
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
