package com.ssafy.double_bean.story.dto;

import com.ssafy.double_bean.story.model.entity.SpotEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SpotInsertRequestDto(
        UUID previousSpotUuid,
        @NotNull(message = "latitude is required.")
        @Min(value = 33, message = "latitude must bigger than 33.0")
        @Max(value = 43, message = "latitude must smaller than 43.0")
        double latitude,
        @NotNull(message = "longitude is required.")
        @Min(value = 124, message = "longitude must bigger than 33.0")
        @Max(value = 132, message = "longitude must smaller than 43.0")
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
