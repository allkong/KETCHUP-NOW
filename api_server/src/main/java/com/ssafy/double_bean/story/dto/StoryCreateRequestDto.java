package com.ssafy.double_bean.story.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

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
}
