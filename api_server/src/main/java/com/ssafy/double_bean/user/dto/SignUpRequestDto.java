package com.ssafy.double_bean.user.dto;

public record SignUpRequestDto(String loginId, String rawPassword, String nickname) {
}
