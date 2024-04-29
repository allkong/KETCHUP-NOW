package com.ssafy.double_bean.auth.dto;

import com.ssafy.double_bean.auth.service.AuthService;

public record SingleTokenResponseDto(
    AuthService.TokenType type,
    String token
) {
}
