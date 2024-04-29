package com.ssafy.double_bean.auth.dto;

import com.ssafy.double_bean.auth.service.AuthService;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "단일 토큰에 대한 응답 DTO")

public record SingleTokenResponseDto(
        @Schema(description = "토큰 타입 (ACCESS, REFRESH)", example = "REFRESH")
        AuthService.TokenType type,
        @Schema(description = "토큰 문자열")
        String token
) {
}
