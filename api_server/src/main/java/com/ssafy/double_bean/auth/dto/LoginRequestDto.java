package com.ssafy.double_bean.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "loginId cannot be null or blank")
        String loginId,
        @NotBlank(message = "password cannot be null or blank")
        String password) {
}