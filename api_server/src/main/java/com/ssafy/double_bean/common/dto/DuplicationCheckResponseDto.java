package com.ssafy.double_bean.common.dto;

public record DuplicationCheckResponseDto(
        String key,
        String value,
        boolean isDuplicated
) {
}
