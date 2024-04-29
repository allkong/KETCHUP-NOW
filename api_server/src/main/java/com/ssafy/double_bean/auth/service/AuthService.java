package com.ssafy.double_bean.auth.service;

import com.ssafy.double_bean.auth.dto.LoginRequestDto;
import com.ssafy.double_bean.auth.dto.TokenResponseDto;

public interface AuthService {
	// 로그인 정보를 받아 JWT Access/Refresh token을 발급한다.
	TokenResponseDto getTokenWithLoginInfo(LoginRequestDto dto);

	// Refresh token을 받아 JWT Refresh token을 발급한다.
	TokenResponseDto getTokenWithRefreshToken(String token);

	// Refresh token에 대한 무효화를 수행한다.
	void revokeRefreshToken(String token);
}
