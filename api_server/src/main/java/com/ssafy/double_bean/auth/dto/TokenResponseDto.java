package com.ssafy.double_bean.auth.dto;

public record TokenResponseDto (
	String accessToken,
	String refreshToken
) { }
