package com.ssafy.double_bean.auth.dto;

import com.ssafy.double_bean.auth.service.AuthService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "단일 토큰에 대한 DTO")

public record SingleTokenDto(
        @NotNull(message = "type cannot be null")
        @Schema(description = "토큰 타입 (ACCESS, REFRESH)", example = "REFRESH")
        AuthService.TokenType type,
        @NotBlank(message = "token cannot be blank")
        @Schema(description = "토큰 문자열")
        String token
) {
}
