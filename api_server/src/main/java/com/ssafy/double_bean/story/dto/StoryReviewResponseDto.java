package com.ssafy.double_bean.story.dto;

import com.ssafy.double_bean.story.model.entity.StoryReviewEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoryReviewResponseDto(
        UUID uuid,
        UUID storyUuid,
        UUID userUuid,
        String storyTitle,
        String authorNickname,
        String title,
        String content,
        int score,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static StoryReviewResponseDto fromEntity(StoryReviewEntity entity) {
        return new StoryReviewResponseDto(entity.getUuid(), entity.getStoryUuid(), entity.getUserUuid(),
                entity.getStoryTitle(), entity.getUserNickname(), entity.getTitle(), entity.getContent(),
                entity.getScore(), entity.getCreatedAt(), entity.getModifiedAt());
    }
}
