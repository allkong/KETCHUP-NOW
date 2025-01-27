package com.ssafy.double_bean.story.dto;

import com.ssafy.double_bean.story.model.entity.StoryEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.net.URI;

@Schema(description = "스토리 생성 요청 API")
public record StoryCreateRequestDto(
        @Schema(description = "스토리 제목")
        @NotBlank(message = "title cannot be blank.")
        String title,
        @Schema(description = "스토리 설명")
        @NotBlank(message = "decription cannot be blank.")
        String description,
        @Schema(description = "시/도 이름")
        String sido,
        @Schema(description = "군/구 이름")
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

    public StoryEntity toRequestEntity() {
        return this.toRequestEntity(null, null);
    }
}
