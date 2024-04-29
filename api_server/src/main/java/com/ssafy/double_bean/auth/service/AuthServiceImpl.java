package com.ssafy.double_bean.auth.service;

import static com.ssafy.double_bean.util.DatetimeUtil.DAYS;
import static com.ssafy.double_bean.util.DatetimeUtil.HOURS;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import com.ssafy.double_bean.auth.dto.LoginRequestDto;
import com.ssafy.double_bean.auth.dto.TokenResponseDto;
import com.ssafy.double_bean.exception.ErrorCode;
import com.ssafy.double_bean.exception.HttpResponseException;
import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthServiceImpl implements AuthService{
	private SecretKey secretKey;
	private final UserService userService;

	public AuthServiceImpl(@Value("${auth.jwt.secretKey}") String rawSecretKey, UserService userService) {
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(rawSecretKey));
		this.userService = userService;
	}

	@Override
	public TokenResponseDto getTokenWithLoginInfo(LoginRequestDto dto) {
		// 우선 loginId로 사용자를 찾아서, 없으면 접근 권한이 없음을 응답
		UserEntity foundUser = userService.findUserByLoginId(dto.loginId())
				.orElseThrow(() -> new HttpResponseException(ErrorCode.UNKNOWN_USER));

		// 비밀번호 일치 여부 확인
		if (!BCrypt.checkpw(dto.password(), foundUser.getPassword())) {
			throw new HttpResponseException(ErrorCode.UNKNOWN_USER);
		}

		String accessToken = createToken(foundUser.getUuid().toString(), TokenType.ACCESS);
		String refreshToken = createToken(foundUser.getUuid().toString(), TokenType.REFRESH);
		return new TokenResponseDto(accessToken, refreshToken);
	}

	@Override
	public TokenResponseDto getTokenWithRefreshToken(String token) {
		return null;
	}

	@Override
	public void revokeRefreshToken(String token) {
		throw new UnsupportedOperationException();
	}



	private record JsonClaim(String identifier, TokenType type) {
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
