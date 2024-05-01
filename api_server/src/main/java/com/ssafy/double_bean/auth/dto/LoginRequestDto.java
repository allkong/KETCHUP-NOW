package com.ssafy.double_bean.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequestDto(
        @Schema(description = "로그인 ID", example = "ssafy")
        @NotBlank(message = "loginId cannot be null or blank")
        String loginId,
        @Schema(description = "비밀번호", example = "password123!")
        @NotBlank(message = "password cannot be null or blank")
        String password) {
}