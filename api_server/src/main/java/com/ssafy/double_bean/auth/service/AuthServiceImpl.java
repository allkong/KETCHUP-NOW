package com.ssafy.double_bean.auth.service;

import com.ssafy.double_bean.auth.dto.LoginRequestDto;
import com.ssafy.double_bean.auth.dto.SingleTokenDto;
import com.ssafy.double_bean.auth.dto.TokenResponseDto;
import com.ssafy.double_bean.exception.ErrorCode;
import com.ssafy.double_bean.exception.HttpResponseException;
import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

import static com.ssafy.double_bean.util.constant.TimeUnit.DAYS;
import static com.ssafy.double_bean.util.constant.TimeUnit.HOURS;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final SecretKey secretKey;

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
    public SingleTokenDto getTokenWithRefreshToken(String token) {
        UUID uuid = parseUuidFrom(token);
        String accessToken = createToken(uuid.toString(), TokenType.ACCESS);
        return new SingleTokenDto(TokenType.ACCESS, accessToken);
    }

    @Override
    public void revokeRefreshToken(String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Authentication getAuthentication(String token, String uuid) {
        User principal = new User(uuid, "", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(principal, token, new ArrayList<>());
    }

    @Override
    public UUID validateAndGetUuid(String token, TokenType requiredType) {
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            String tokenType = claims.get("type", String.class);
            if (!requiredType.name().equals(tokenType)) {
                throw new HttpResponseException(ErrorCode.BAD_TOKEN_TYPE);
            }
            String id = claims.get("id", String.class);
            return UUID.fromString(id);
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            throw new HttpResponseException(ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
            // 토큰 파싱 실패
            throw new HttpResponseException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String createToken(String identifier, TokenType type) {
        JsonClaim claim = new JsonClaim(identifier, type);
        return switch (type) {
            case ACCESS -> createToken(claim, 3 * HOURS);
            case REFRESH -> createToken(claim, 30 * DAYS);
            default -> throw new IllegalStateException("Unknown token type : " + type);
        };
    }

    public String createToken(JsonClaim claim, long expiration) {
        return Jwts.builder().expiration(new Date(System.currentTimeMillis() + expiration)).claims(claim.toMap())
                .signWith(secretKey).compact();
    }

    private UUID parseUuidFrom(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        return UUID.fromString(claims.get("id", String.class));
    }

    private record JsonClaim(String identifier, TokenType type) {
        public Map<String, Object> toMap() {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", identifier);
            map.put("type", type);
            return map;
        }
    }
}
