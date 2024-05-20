package com.ssafy.double_bean.story.dto;

import com.ssafy.double_bean.story.model.entity.SpotEntity;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record SpotInsertRequestDto(
        UUID previousSpotUuid,
        double latitude,
        double longitude,
        @NotBlank(message = "title is required.")
        String title,
        @NotBlank(message = "description is required.")
        String description
) {
    public SpotEntity toRequestEntity() {
        SpotEntity entity = new SpotEntity();
        entity.setLatitude(latitude);
        entity.setLongitude(longitude);
        entity.setTitle(title);
        entity.setDescription(description);
        return entity;
    }
}
