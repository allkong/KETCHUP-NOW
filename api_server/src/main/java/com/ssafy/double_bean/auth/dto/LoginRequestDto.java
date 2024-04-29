package com.ssafy.double_bean.auth.dto;

public record LoginRequestDto(String loginId, String rawPassword) {
}