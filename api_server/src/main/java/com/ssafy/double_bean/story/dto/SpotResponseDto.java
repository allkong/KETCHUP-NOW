package com.ssafy.double_bean.story.dto;

import com.ssafy.double_bean.story.model.entity.SpotEntity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

public record SpotResponseDto(
        UUID uuid,
        double latitude,
        double longitude,
        double orderIndex,
        String title,
        String description,
        URI imageUri,
        URI thumbnailUri,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        SpotEntity.EventType eventType,
        URI eventImageUri,
        URI eventThumbnailImageUri,
        String jsonEventContent,
        UUID storyUuid
) {
    public static SpotResponseDto fromEntity(SpotEntity entity) {
        return new SpotResponseDto(entity.getUuid(), entity.getLatitude(), entity.getLongitude(), entity.getOrderIndex(),
                entity.getTitle(), entity.getDescription(), entity.getImageUri(), entity.getThumbnailImageUri(),
                entity.getCreatedAt(), entity.getModifiedAt(), entity.getEventType(), entity.getEventImageUri(),
                entity.getEventThumbnailImageUri(), entity.getJsonEventContent(), entity.getStoryUuid());
    }
}
