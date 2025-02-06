package store.novabook.auth.jwt;

import java.security.Key;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import store.novabook.auth.entity.AccessTokenInfo;

@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {
	private final Environment env;
	private Key key;
	private static final String UUID = "uuid";

	@Override
	public void afterPropertiesSet() {
		String secret = env.getProperty("JWT-SECRET");
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String createAccessToken(UUID uuid) {
		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, uuid.toString())
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String createAccessTokenFromRefreshToken(AccessTokenInfo accessTokenInfo) {
		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.claim(UUID, accessTokenInfo.getUuid())
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

	public String getUUID(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
			return claims.get("uuid", String.class);
		} catch (JwtException e) {
			throw new JwtException("Invalid token");
		}
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
