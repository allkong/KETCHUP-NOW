package com.ssafy.double_bean.story.dto;

import com.ssafy.double_bean.story.model.entity.StoryReviewEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record StoryReviewCreateRequestDto(
        @NotBlank(message = "title cannot be blank.")
        String title,
        @NotBlank(message = "content cannot be blank.")
        String content,
        @Min(value = 0, message = "score must be bigger than 0.")
        @Max(value = 5, message = "score must be smaller than 5.")
        int score
) {
    public StoryReviewEntity toRequestEntity(UUID storyUuid, UUID userUuid) {
        StoryReviewEntity entity = new StoryReviewEntity();
        entity.setStoryUuid(storyUuid);
        entity.setUserUuid(userUuid);
        entity.setTitle(title);
        entity.setContent(content);
        entity.setScore(score);
        return entity;
    }
}
