package com.ssafy.double_bean.user.dto;

import com.ssafy.double_bean.user.model.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(UUID uuid, String loginId, String nickname, LocalDateTime createdAt,
		LocalDateTime modifiedAt) {
	public static UserResponseDto fromEntity(UserEntity entity) {
		return new UserResponseDto(entity.getUuid(), entity.getLoginId(), entity.getNickname(), entity.getCreatedAt(), entity.getModifiedAt());
	}
}
