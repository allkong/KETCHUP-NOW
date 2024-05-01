package com.ssafy.double_bean.auth.service;

import com.ssafy.double_bean.auth.dto.LoginRequestDto;
import com.ssafy.double_bean.auth.dto.SingleTokenDto;
import com.ssafy.double_bean.auth.dto.TokenResponseDto;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface AuthService {
    // 로그인 정보를 받아 JWT Access/Refresh token을 발급한다.
    TokenResponseDto getTokenWithLoginInfo(LoginRequestDto dto);

    // Refresh token을 받아 JWT access token을 발급한다.
    SingleTokenDto getTokenWithRefreshToken(String token);

    // Refresh token에 대한 무효화를 수행한다.
    void revokeRefreshToken(String token);

    Authentication getAuthentication(String token, String uuid);

    UUID validateAndGetUuid(String token, TokenType requiredType);

    enum TokenType {
        ACCESS, REFRESH
    }
}
