package com.ssafy.double_bean.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(UUID uuid, String loginId, String nickname, LocalDateTime createdAt,
		LocalDateTime modifiedAt) {
}
