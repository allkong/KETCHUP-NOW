package com.ssafy.double_bean.story_play.dto;

import com.ssafy.double_bean.story_play.model.entity.StoryPlayingLogEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoryPlayingLogRespDto(
        UUID uuid,
        UUID storyPlayingUuid,
        UUID spotUuid,
        UUID userUuid,
        String jsonEventContent,
        LocalDateTime createdAt
) {
    public static StoryPlayingLogRespDto fromEntity(StoryPlayingLogEntity entity) {
        return new StoryPlayingLogRespDto(entity.getUuid(), entity.getStoryPlayingUuid(), entity.getSpotUuid(),
                entity.getUserUuid(), entity.getJsonEventContent(), entity.getCreatedAt());
    }
}
