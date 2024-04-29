package com.ssafy.double_bean.auth.service;

import static com.ssafy.double_bean.util.DatetimeUtil.DAYS;
import static com.ssafy.double_bean.util.DatetimeUtil.HOURS;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	private SecretKey secretKey;

	public JWTService(@Value("${auth.jwt.secretKey}") String rawSecretKey) {
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(rawSecretKey));
	}

	public static enum TokenType {
		ACCESS, REFRESH
	}

	private static record JsonClaim(String identifier, TokenType type) {
		public Map<String, Object> toMap() {
			HashMap<String, Object> map = new HashMap<>();
			map.put("id", identifier);
			map.put("type", type);
			return map;
		}
	}

	public String createToken(String identifier, TokenType type) {
		JsonClaim claim = new JsonClaim(identifier, type);
		switch (type) {
		case ACCESS:
			return createToken(claim, 3 * HOURS);
		case REFRESH:
			return createToken(claim, 30 * DAYS);
		default:
			throw new IllegalStateException("Unknown token type : " + type);
		}
	}

	public String createToken(JsonClaim claim, long expiration) {
		return Jwts.builder().expiration(new Date(System.currentTimeMillis() + expiration)).claims(claim.toMap())
				.signWith(secretKey).compact();
	}
}
