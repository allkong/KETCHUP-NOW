package com.ssafy.double_bean.story.dto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ssafy.double_bean.story.model.entity.StoryEntity;
import com.ssafy.double_bean.story.model.entity.StoryEntity.StoryStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스토리 응답 DTO")
public record StoryResponseDto(
		@Schema(description = "고유 식별자")
		UUID uuid,
		UUID storyBaseUuid,
		String authorNickname,
		UUID authorUuid,
		@Schema(description = "버전")
		int version,
		@Schema(description = "상태")
		StoryStatus status,
		@Schema(description = "제목")
		String title,
		@Schema(description = "설명")
		String description,
		@Schema(description = "시/도")
		String sido,
		@Schema(description = "군/구")
		String gungu,
		@Schema(description = "이미지 URI")
		URI imageUri,
		@Schema(description = "썸네일 URI")
		URI thumbnailImageUri,
		@Schema(description = "생성 일시")
		LocalDateTime createdAt,
		@Schema(description = "최종 수정 일시")
		LocalDateTime modifiedAt
		) {
	public static StoryResponseDto fromEntity(StoryEntity entity) {
		return new StoryResponseDto(
				entity.getUuid(), entity.getStoryBaseUuid(), entity.getAuthorNickname(), entity.getAuthorUuid(), entity.getVersion(),
				entity.getStatus(), entity.getTitle(), entity.getDescription(), entity.getSido(),entity.getGungu(),
				entity.getImageUri(), entity.getThumbnailImageUri(), entity.getCreatedAt(), entity.getModifiedAt());
	}
}
