package com.ssafy.double_bean.story.dto;

import com.ssafy.double_bean.story.model.entity.StoryEntity;
import jakarta.validation.constraints.NotBlank;

import java.net.URI;

public record StoryUpdateRequestDto(
        @NotBlank(message = "title cannot be blank.")
        String title,
        @NotBlank(message = "description cannot be blank.")
        String description,
        String sido,
        String gungu) {
    public StoryEntity toRequestEntity(URI imageUri, URI thumbnailImageUri) {
        StoryEntity requestEntity = new StoryEntity();
        requestEntity.setTitle(title);
        requestEntity.setDescription(description);
        requestEntity.setSido(sido);
        requestEntity.setGungu(gungu);
        requestEntity.setImageUri(imageUri);
        requestEntity.setThumbnailImageUri(thumbnailImageUri);
        return requestEntity;
    }
}
