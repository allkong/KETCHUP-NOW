package com.ssafy.double_bean.story_play.dto;

import com.ssafy.double_bean.story_play.model.entity.StoryPlayingEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoryPlayingResponseDto(
        UUID uuid,
        UUID storyUuid,
        StoryPlayStatus status,
        LocalDateTime startedAt,
        LocalDateTime clearedAt
) {
    public static StoryPlayingResponseDto fromEntity(StoryPlayingEntity entity) {
        StoryPlayStatus status = entity.getClearedAt() == null ? StoryPlayStatus.PLAYING : StoryPlayStatus.CLEARED;
        return new StoryPlayingResponseDto(entity.getUuid(), entity.getStoryUuid(), status,
                entity.getCreatedAt(), entity.getClearedAt());
    }

    public enum StoryPlayStatus {
        PLAYING, CLEARED
    }
}
