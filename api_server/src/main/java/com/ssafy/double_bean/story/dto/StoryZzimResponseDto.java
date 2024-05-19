package com.ssafy.double_bean.story.dto;

import com.ssafy.double_bean.story.model.entity.StoryZzimEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoryZzimResponseDto(
        UUID uuid,
        UUID storyUuid,
        UUID userUuid,
        LocalDateTime createdAt
) {
    public static StoryZzimResponseDto fromEntity(StoryZzimEntity entity) {
        return new StoryZzimResponseDto(entity.getUuid(), entity.getStoryUuid(), entity.getUserUuid(), entity.getCreatedAt());
    }
}
